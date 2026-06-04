//Package
package com.petboarding.View.EditViews;

//Imports
import com.petboarding.Models.Owner;
import com.petboarding.View.DetailViews.PetDetailsScreen;

import javax.swing.*;
import java.awt.*;

public class EditOwnerScreen extends JFrame {
    private PetDetailsScreen parent;

    public EditOwnerScreen(Owner owner, PetDetailsScreen parent) {
        this.parent = parent;
        setTitle("Edit Owner - " + owner.getName());
        setSize(350, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField nameField = new JTextField(owner.getName());
        JTextField phoneField = new JTextField(owner.getPhone());
        JTextField emailField = new JTextField(owner.getEmail());
        JTextField addressField = new JTextField(owner.getAddress());

        //Layout
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);

        JButton deleteButton = new JButton("Delete");
        JButton saveButton = new JButton("Save");


    }
}
