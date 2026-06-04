//Package
package com.petboarding.View.CreateViews;

//Imports
import com.petboarding.Models.Owner;
import com.petboarding.Models.Pet;
import com.petboarding.Database.PetDAO;
import com.petboarding.View.AppContext;
import com.petboarding.View.DetailViews.OwnerSearchDialog;

import javax.swing.*;
import java.awt.*;

public class CreatePetScreen extends JFrame {

    private JTextField nameField, speciesField, ageField;
    private JTextArea notesArea;

    private JTextField ownerIdField;
    private JButton searchOwnerButton;
    private JButton saveButton;

    private final AppContext context;

    public CreatePetScreen(AppContext context) {
        this.context = context;

        setTitle("Create New Pet");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2, 5, 5));

        nameField = new JTextField();
        speciesField = new JTextField();
        ageField = new JTextField();

        //Set notes field to multiple lines
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        ownerIdField = new JTextField();
        ownerIdField.setEditable(false);        //Owner ID is grabbed from OwnerSearchDialog, ensures correct relationships in DB

        searchOwnerButton = new JButton("Find Owner");
        saveButton = new JButton("Save Pet");

        add(new JLabel("Owner ID:"));
        add(ownerIdField);

        add(new JLabel(""));
        add(searchOwnerButton);

        add(new JLabel("Pet Name:"));
        add(nameField);

        add(new JLabel("Species(Dog/Cat):"));
        add(speciesField);

        add(new JLabel("Age:"));
        add(ageField);

        add(new JLabel("Notes:"));
        add(new JScrollPane(notesArea));

        add(saveButton);




        searchOwnerButton.addActionListener(e -> openOwnerSearch());
        saveButton.addActionListener(e -> savePet());
    }

    private void openOwnerSearch() {
        new OwnerSearchDialog(context.ownerRepository, this).setVisible(true);
    }

    public void setSelectedOwner(Owner owner) {
        ownerIdField.setText(String.valueOf(owner.getOwnerId()));
    }

    private void savePet() {
        try {
            String name = nameField.getText().trim();
            String species = speciesField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String notes = notesArea.getText().trim();
            int ownerId = Integer.parseInt(ownerIdField.getText().trim());

            //Create pet with temporary ID, to be set once added to DB
            Pet newPet = new Pet(
                    -1,
                    ownerId,
                    name,
                    species,
                    age,
                    notes
            );

            // Insert into DB and get generated ID
            int generatedId = PetDAO.addPet(newPet);
            newPet.setPetId(generatedId);

            // Add to repository and refresh main pet table
            context.petRepository.addPet(newPet);
            context.petTablePanel.loadPetsIntoTable();

            JOptionPane.showMessageDialog(this, "Pet created successfully!");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving pet: " + e.getMessage());
        }
    }
}
