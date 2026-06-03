package com.petboarding.View.CreateViews;

import com.petboarding.Models.Owner;
import com.petboarding.Repository.OwnerRepository;
import com.petboarding.Database.OwnerDAO;
import com.petboarding.View.DataViews.OwnerTablePanel;

import javax.swing.*;
import java.awt.*;

public class CreateOwnerScreen extends JFrame {

    private JTextField nameField, phoneField, emailField, addressField;

    private JButton saveButton;

    private final OwnerRepository ownerRepository;
    private final OwnerTablePanel ownerTablePanel;

    public CreateOwnerScreen(OwnerRepository ownerRepository, OwnerTablePanel ownerTablePanel) {
        this.ownerRepository = ownerRepository;
        this.ownerTablePanel = ownerTablePanel;

        setTitle("Create Owner");
        setSize(400, 350);
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

            ownerRepository.addOwner(newOwner);
            ownerTablePanel.loadOwnersIntoTable(ownerRepository);

            JOptionPane.showMessageDialog(null, newOwner.getName() + " Owner has been created");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving new owner: " + e.getMessage());
        }
    }
}
