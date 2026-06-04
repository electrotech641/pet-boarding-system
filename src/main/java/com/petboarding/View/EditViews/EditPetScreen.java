//Packages
package com.petboarding.View.EditViews;

//Imports
import com.petboarding.Models.Pet;
import com.petboarding.Database.PetDAO;
import com.petboarding.View.AppContext;
import com.petboarding.View.DetailViews.PetDetailsScreen;
import javax.swing.*;
import java.awt.*;

public class EditPetScreen extends JFrame{
    private PetDetailsScreen parent;
    private final AppContext context;
    private Pet pet;

    private JTextField nameField, speciesField, ageField, notesField;

    public EditPetScreen(Pet pet, PetDetailsScreen parent, AppContext context) {
        this.parent = parent;
        this.context = context;
        this.pet = pet;
        setTitle("Edit Pet - " + pet.getName());
        setSize(350, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        nameField = new JTextField(pet.getName());
        speciesField = new JTextField(pet.getSpecies());
        ageField = new JTextField(String.valueOf(pet.getAge()));
        notesField = new JTextField(pet.getNotes());

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
        saveButton.addActionListener(e -> saveChanges());

        add(panel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }

    private void saveChanges() {
        try {
            pet.setName(nameField.getText());
            pet.setSpecies(speciesField.getText());
            pet.setAge(Integer.parseInt(ageField.getText()));
            pet.setNotes(notesField.getText());

            PetDAO.updatePet(pet);

            //Refresh the details screen and main pet table
            parent.refreshDetails();
            context.refreshPets();

            JOptionPane.showMessageDialog(this, "Pet updated successfully.");
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating pet: " + ex.getMessage());
        }
    }
}
