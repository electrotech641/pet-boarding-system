//Packages
package com.petboarding.View.DetailViews;

//Imports
import com.petboarding.Repository.OwnerRepository;
import com.petboarding.Models.Owner;
import com.petboarding.Models.Pet;
import com.petboarding.Database.PetDAO;
import com.petboarding.Models.User;
import com.petboarding.Repository.PetRepository;
import com.petboarding.Repository.StayRepository;
import com.petboarding.View.CreateViews.CheckInStayScreen;
import com.petboarding.View.DataViews.CurrentStaysTablePanel;
import com.petboarding.View.EditViews.EditPetScreen;
import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class PetDetailsScreen extends JFrame {

    private User currentUser;
    private JLabel nameLabel, speciesLabel, ageLabel, ownerLabel, notesLabel, boardedLabel;
    private JPanel staysPanel;
    private String currentlyBoarded = "";
    private Pet pet;

    private final StayRepository stayRepository;
    private final CurrentStaysTablePanel currentStaysTablePanel;
    private final PetRepository petRepository;


    //Construct pet details screen
    public PetDetailsScreen(
            Pet pet,
            User user,
            OwnerRepository ownerRepository,
            StayRepository stayRepository,
            CurrentStaysTablePanel currentStaysTablePanel,
            PetRepository petRepository) throws SQLException {
        this.currentUser = user;
        this.pet = pet;
        this.stayRepository = stayRepository;
        this.currentStaysTablePanel = currentStaysTablePanel;
        this.petRepository = petRepository;
        Owner owner = ownerRepository.getOwnerById(pet.getOwnerId());
        setTitle("Pet Details - " + pet.getName());
        setSize(400, 700);
        setLocationRelativeTo(null);

        //create new panel for pet details
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //Layout labels and data on screen
        nameLabel = new JLabel("Name: " + pet.getName());
        speciesLabel = new JLabel("Species: " + pet.getSpecies());
        ageLabel = new JLabel("Age: " + pet.getAge());
        ownerLabel = new JLabel("Owner: " + owner.getName());
        notesLabel = new JLabel("Notes: " + pet.getNotes());
        boardedLabel = new JLabel("Currently boarded: " + currentlyBoarded);

        //Layout
        panel.add(new JLabel("ID: " + pet.getPetId()));
        panel.add(nameLabel);
        panel.add(speciesLabel);
        panel.add(ageLabel);
        panel.add(ownerLabel);
        panel.add(notesLabel);
        panel.add(new JLabel(" "));
        panel.add(boardedLabel);
        panel.add(new JLabel(" "));
        panel.add(new JLabel("Stay History:"));

        staysPanel = new JPanel();
        staysPanel.setLayout(new BoxLayout(staysPanel, BoxLayout.Y_AXIS));
        panel.add(staysPanel);

        loadStayHistory();

        if ((currentUser.isStaff() || currentUser.isAdmin()) && currentlyBoarded.equalsIgnoreCase("NO")) {
            JButton checkInButton = new JButton("Check In");

            checkInButton.addActionListener(e -> {
                new CheckInStayScreen(pet, currentUser, stayRepository, currentStaysTablePanel, this).setVisible(true);
            });

            panel.add(Box.createVerticalStrut(10));
            panel.add(checkInButton);
        }

        /*
            Check if current user is an ADMIN and display appropriate buttons
         */
        if (currentUser.isAdmin()) {
            //Edit pet button for admins only
            JButton editButton = new JButton("Edit Pet");
            editButton.addActionListener(e -> {
                new EditPetScreen(pet, this).setVisible(true);
            });

            //Owner details button for admins only
            JButton ownerButton = new JButton("Owner Details");
            ownerButton.addActionListener(e -> {
                new OwnerDetailsScreen(owner, currentUser).setVisible(true);
            });

            panel.add(Box.createVerticalStrut(10));
            panel.add(editButton);

            panel.add(Box.createVerticalStrut(10));
            panel.add(ownerButton);
        }

        add(panel);
    }

    //Load this pet's stays history from database
    private void loadStayHistory() throws SQLException {
        staysPanel.removeAll();

        List<String> stays = PetDAO.getStayHistory(pet.getPetId());

        if (stays.isEmpty()) {
            staysPanel.add(new JLabel(" - No stays found"));
        } else {
            for (String stay : stays) {
                staysPanel.add(new JLabel(" - " + stay));
            }
        }
        setCurrentlyBoarded();
        staysPanel.revalidate();
        staysPanel.repaint();
    }

    //Refresh details in UI
    public void refreshDetails() throws SQLException {
        nameLabel.setText("Name: " + pet.getName());
        speciesLabel.setText("Species: " + pet.getSpecies());
        ageLabel.setText("Age: " + pet.getAge());
        notesLabel.setText("Notes: " + pet.getNotes());

        loadStayHistory();
    }

    public void setCurrentlyBoarded() throws SQLException {
        if (PetDAO.isPetCurrentlyBoarded(pet.getPetId())) {
            currentlyBoarded = "Yes";
        }
        else {
            currentlyBoarded = "No";
        }

        //Set boardedLabel to show current boarding status
        boardedLabel.setText("Currently boarded: " + currentlyBoarded);
    }
}