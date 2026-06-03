//Package
package com.petboarding.View.DetailViews;

//Imports
import com.petboarding.Models.Stay;
import com.petboarding.Database.StayDAO;
import com.petboarding.Models.Pet;
import com.petboarding.Models.Owner;
import com.petboarding.Models.User;
import com.petboarding.Repository.PetRepository;
import com.petboarding.Repository.OwnerRepository;
import com.petboarding.Repository.StayRepository;
import com.petboarding.View.DataViews.CurrentStaysTablePanel;
import com.petboarding.View.EditViews.EditStayScreen;

import javax.swing.*;
import java.sql.SQLException;

public class StayDetailsScreen extends JFrame {

    private final Stay stay;
    private final User currentUser;
    private final CurrentStaysTablePanel currentStaysTablePanel;
    private final StayRepository stayRepository;

    private JButton checkOutButton;
    private final JLabel checkInLabel;
    private final JLabel checkOutLabel;


    public StayDetailsScreen(Stay stay,
                             User user,
                             StayRepository stayRepository,
                             PetRepository petRepository,
                             OwnerRepository ownerRepository,
                             CurrentStaysTablePanel currentStaysTablePanel) {

        this.stay = stay;
        this.stayRepository = stayRepository;
        this.currentUser = user;
        this.currentStaysTablePanel = currentStaysTablePanel;
        boolean notCheckedOut = stay.getStatus().equalsIgnoreCase("In Progress");

        Pet pet = petRepository.getPetById(stay.getPetId());
        Owner owner = ownerRepository.getOwnerById(pet.getOwnerId());

        setTitle("Stay Details - Stay #" + stay.getStayId());
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Labels
        JLabel petLabel = new JLabel("Pet: " + pet.getName());
        JLabel ownerLabel = new JLabel("Owner: " + owner.getName());
        checkInLabel = new JLabel("Check-In: " + stay.getCheckInDate());
        checkOutLabel = new JLabel("Check-Out: " + stay.getCheckOutDate());

        panel.add(new JLabel("Stay ID: " + stay.getStayId()));
        panel.add(petLabel);
        panel.add(ownerLabel);
        panel.add(checkInLabel);
        panel.add(checkOutLabel);

        panel.add(new JLabel(" "));

        // Admin-only buttons
        if (currentUser.isAdmin() || currentUser.getRole().equals("STAFF")) {

            JButton editStayButton = new JButton("Edit Stay");
            editStayButton.addActionListener(e -> {
                new EditStayScreen(stay, this, stayRepository, currentStaysTablePanel).setVisible(true);
            });

            checkOutButton = new JButton("Check-Out");
            checkOutButton.addActionListener(e -> {
                try {
                    handleCheckOut(pet);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });


            JButton petDetailsButton = new JButton("Pet Details");
            petDetailsButton.addActionListener(e -> {
                try {
                    new PetDetailsScreen(pet, currentUser, ownerRepository, stayRepository, currentStaysTablePanel, petRepository).setVisible(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });

            JButton ownerDetailsButton = new JButton("Owner Details");
            ownerDetailsButton.addActionListener(e -> {
                new OwnerDetailsScreen(owner, currentUser).setVisible(true);
            });

            panel.add(Box.createVerticalStrut(10));
            panel.add(editStayButton);

            //Check if pet has already been checked out
            if (notCheckedOut) {
                panel.add(Box.createVerticalStrut(10));
                panel.add(checkOutButton);
            }


            panel.add(Box.createVerticalStrut(10));
            panel.add(petDetailsButton);

            panel.add(Box.createVerticalStrut(10));
            panel.add(ownerDetailsButton);
        }

        add(panel);
    }

    private void handleCheckOut(Pet pet) throws SQLException {
        //Check if pet is already checked out
        if (stay.getStatus().equalsIgnoreCase("Completed")) {
            JOptionPane.showMessageDialog(this, "This stay is already checked out.", "Already Completed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to check-out this stay?", "Confirm Check-Out", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        //Set stay check-out date to today's date and update status
        String today = java.time.LocalDate.now().toString();
        stay.setCheckOutDate(today);
        stay.setStatus("Completed");

        //Update stay in database and details UI
        StayDAO.updateStay(stay);
        refreshDetails();
        checkOutButton.setVisible(false);

        //Remove stay from CurrentStays table and refresh the table on the main screen
        currentStaysTablePanel.getStayRepository().removeStay(stay.getStayId());
        currentStaysTablePanel.loadStaysIntoTable(currentStaysTablePanel.getStayRepository());

        //Success message box
        JOptionPane.showMessageDialog(this, pet.getName() + " checked out successfully!", "Check-Out Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    // Refresh UI after editing
    public void refreshDetails() {
        checkInLabel.setText("Check-In: " + stay.getCheckInDate());
        checkOutLabel.setText("Check-Out: " + stay.getCheckOutDate());
    }
}
