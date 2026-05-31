//Packages
package com.petboarding.View;

//Imports
import com.petboarding.Models.Pet;
import com.petboarding.Database.PetDAO;
import com.petboarding.View.DetailViews.PetDetailsScreen;
import javax.swing.*;
import java.awt.*;

public class EditPetScreen extends JFrame{
    private PetDetailsScreen parent;

    public EditPetScreen(Pet pet, PetDetailsScreen parent) {
        this.parent = parent;
        setTitle("Edit Pet - " + pet.getName());
        setSize(350, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField nameField = new JTextField(pet.getName());
        JTextField speciesField = new JTextField(pet.getSpecies());
        JTextField ageField = new JTextField(String.valueOf(pet.getAge()));
        JTextField notesField = new JTextField(pet.getNotes());

        //Layout
        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Species:"));
        panel.add(speciesField);

        panel.add(new JLabel("Age:"));
        panel.add(ageField);

        panel.add(new JLabel("Notes:"));
        panel.add(notesField);

        JButton saveButton = new JButton("Save Changes");

        //Save changes button functionality, update pet object and attempt to update DB via DAO
        saveButton.addActionListener(e -> {
            try {
                pet.setName(nameField.getText());
                pet.setSpecies(speciesField.getText());
                pet.setAge(Integer.parseInt(ageField.getText()));
                pet.setNotes(notesField.getText());

                PetDAO.updatePet(pet);

                // ⭐ Refresh the details screen
                parent.refreshDetails();

                JOptionPane.showMessageDialog(this, "Pet updated successfully.");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating pet: " + ex.getMessage());
            }
        });

        add(panel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }
}
