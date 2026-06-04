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

    private JLabel idLabel,nameLabel, speciesLabel, ageLabel, ownerLabel, notesLabel, boardedLabel;
    private JPanel staysHistoryPanel;
    private JButton checkInButton, editButton, ownerButton;

    private String currentlyBoarded = "";
    private Pet pet;
    private EditPetScreen editPetScreen;

    //Construct pet details screen
    public PetDetailsScreen(Pet pet, AppContext context) throws SQLException {
        this.pet = pet;
        this.context = context;

        setTitle("Pet Details");
        setSize(400, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        idLabel = new JLabel();
        nameLabel = new JLabel();
        speciesLabel = new JLabel();
        ageLabel = new JLabel();
        ownerLabel = new JLabel();
        notesLabel = new JLabel();
        boardedLabel = new JLabel();

        checkInButton = new JButton("Check In");
        checkInButton.addActionListener(e -> {
            new CheckInStayScreen(pet, this, context).setVisible(true);
        });

        editButton = new JButton("Edit Pet");
        editButton.addActionListener(e -> openEditPetScreen());

        ownerButton = new JButton("Owner Details");
        ownerButton.addActionListener(e -> {
            Owner owner = context.ownerRepository.getOwnerById(pet.getOwnerId());
            new OwnerDetailsScreen(owner, context).setVisible(true);
        });


        panel.add(idLabel);
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

        add(panel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(checkInButton);

        panel.add(Box.createVerticalStrut(10));
        panel.add(editButton);

        panel.add(Box.createVerticalStrut(10));
        panel.add(ownerButton);


        refreshDetails(); // populate UI
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

        if (pet == null) {
            checkInButton.setVisible(false);
            editButton.setVisible(false);
            ownerButton.setVisible(false);

            idLabel.setText("ID: N/A");
            nameLabel.setText("Name: N/A");
            speciesLabel.setText("Species: N/A");
            ageLabel.setText("Age: N/A");
            ownerLabel.setText("Owner: N/A");
            notesLabel.setText("Notes: N/A");
            boardedLabel.setText("Currently boarded: N/A");

            staysHistoryPanel.removeAll();
            staysHistoryPanel.add(new JLabel(" - No stays found"));
            staysHistoryPanel.revalidate();
            staysHistoryPanel.repaint();
            return;
        }

        Owner owner = context.ownerRepository.getOwnerById(pet.getOwnerId());

        idLabel.setText("ID: " + pet.getPetId());
        nameLabel.setText("Name: " + pet.getName());
        speciesLabel.setText("Species: " + pet.getSpecies());
        ageLabel.setText("Age: " + pet.getAge());
        ownerLabel.setText("Owner: " + (owner != null ? owner.getName() : "Unknown"));
        notesLabel.setText("Notes: " + pet.getNotes());

        //load stay history and update currentlyBoarded
        loadStayHistory();

        //Display or hide buttons based on user role and if pet can be checked in
        boolean isAdmin = context.currentUser.isAdmin();
        boolean isStaff = context.currentUser.isStaff();

        boolean canCheckIn =
                (isAdmin || isStaff) &&
                        currentlyBoarded.equalsIgnoreCase("No");

        checkInButton.setVisible(canCheckIn);
        editButton.setVisible((isAdmin || isStaff));
        ownerButton.setVisible((isAdmin || isStaff));
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

    public void loadPet(Pet pet) throws SQLException {
        this.pet = pet;
        refreshDetails();
    }

}
