//Package
package com.petboarding.View.DetailViews;

//Imports
import com.petboarding.View.AppContext;
import com.petboarding.Database.PetDAO;
import com.petboarding.Models.Owner;
import com.petboarding.Models.Pet;
import com.petboarding.View.CreateViews.CheckInStayScreen;
import com.petboarding.View.EditViews.EditPetScreen;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

public class PetDetailsScreen extends JFrame {

    private final AppContext context;

    private JLabel nameLabel, speciesLabel, ageLabel, ownerLabel, notesLabel, boardedLabel;
    private JPanel staysHistoryPanel;
    private String currentlyBoarded = "";
    private Pet pet;
    private EditPetScreen editPetScreen;

    //Construct pet details screen
    public PetDetailsScreen(Pet pet, AppContext context) throws SQLException {

        this.pet = pet;
        this.context = context;

        Owner owner = context.ownerRepository.getOwnerById(pet.getOwnerId());

        setTitle("Pet Details - " + pet.getName());
        setSize(400, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        staysHistoryPanel = new JPanel();
        staysHistoryPanel.setLayout(new BoxLayout(staysHistoryPanel, BoxLayout.Y_AXIS));
        panel.add(staysHistoryPanel);

        loadStayHistory();

        /*
            Check-in button for STAFF and ADMIN only
            Only show if pet is NOT currently boarded
         */
        if ((context.currentUser.isStaff() || context.currentUser.isAdmin()) && currentlyBoarded.equalsIgnoreCase("NO")) {
            JButton checkInButton = new JButton("Check In");

            checkInButton.addActionListener(e -> {
                new CheckInStayScreen(pet,this, context).setVisible(true);
            });

            panel.add(Box.createVerticalStrut(10));
            panel.add(checkInButton);
        }

        /*
            Admin-only buttons
         */
        if (context.currentUser.isAdmin()) {

            //Edit pet button
            JButton editButton = new JButton("Edit Pet");
            editButton.addActionListener(e -> openEditPetScreen());

            //Owner details button
            JButton ownerButton = new JButton("Owner Details");
            ownerButton.addActionListener(e -> {
                new OwnerDetailsScreen(owner, context).setVisible(true);
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
        staysHistoryPanel.removeAll();

        List<String> stays = PetDAO.getStayHistory(pet.getPetId());

        if (stays.isEmpty()) {
            staysHistoryPanel.add(new JLabel(" - No stays found"));
        } else {
            for (String stay : stays) {
                staysHistoryPanel.add(new JLabel(" - " + stay));
            }
        }
        setCurrentlyBoarded();
        staysHistoryPanel.revalidate();
        staysHistoryPanel.repaint();
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

        boardedLabel.setText("Currently boarded: " + currentlyBoarded);
    }

    private void openEditPetScreen() {
        if (editPetScreen != null) {
            editPetScreen.toFront();
            editPetScreen.requestFocus();
            return;
        }

        editPetScreen = new EditPetScreen(pet, this, context);

        editPetScreen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                editPetScreen = null;
            }
        });

        editPetScreen.setVisible(true);
    }
}
