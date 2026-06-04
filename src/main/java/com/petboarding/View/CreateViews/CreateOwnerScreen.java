//Package
package com.petboarding.View.CreateViews;

//Imports
import com.petboarding.Models.Owner;
import com.petboarding.Database.OwnerDAO;
import com.petboarding.View.AppContext;

import javax.swing.*;
import java.awt.*;

public class CreateOwnerScreen extends JFrame {

    private JTextField nameField, phoneField, emailField, addressField;

    private JButton saveButton;
    private final AppContext context;


    public CreateOwnerScreen(AppContext context) {
        this.context = context;

        setTitle("Create Owner");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 5, 5));

        nameField = new JTextField(15);
        phoneField = new JTextField(15);
        emailField = new JTextField(15);
        addressField = new JTextField(15);

        saveButton = new JButton("Save Owner");

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Phone:"));
        add(phoneField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Address:"));
        add(addressField);
        add(saveButton);

        saveButton.addActionListener(e -> saveOwner());
    }

    private void saveOwner() {
        try {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();

            Owner newOwner = new Owner(-1, name, phone, email, address);

            int generatedId = OwnerDAO.addOwner(newOwner);
            newOwner.setOwnerId(generatedId);

            context.ownerRepository.addOwner(newOwner);
            context.refreshOwners();

            JOptionPane.showMessageDialog(null, newOwner.getName() + " Owner has been created");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving new owner: " + e.getMessage());
        }
    }
}
