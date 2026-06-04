//Package
package com.petboarding.View.CreateViews;

//Imports
import com.petboarding.Database.UserDAO;
import com.petboarding.Models.User;
import com.petboarding.Utilities.PasswordUtil;
import com.petboarding.View.AppContext;
import com.petboarding.View.ManageUsersScreen;

import javax.swing.*;
import java.awt.*;

public class CreateUserScreen extends JFrame {

    private final UserDAO userDAO = new UserDAO();
    private final ManageUsersScreen parent;
    private final AppContext context;

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JComboBox<String> roleDropdown;


    public CreateUserScreen(AppContext context, ManageUsersScreen parent) {

        this.context = context;
        this.parent = parent;

        setTitle("Create User");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        roleDropdown = new JComboBox<>(new String[]{"ADMIN", "READ_ONLY", "STAFF"});

        JButton createButton = new JButton("Create");
        createButton.addActionListener(e -> createButtonClicked());

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JLabel("Role:"));
        add(roleDropdown);
        add(new JLabel());
        add(createButton);
    }

    private void createButtonClicked() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = roleDropdown.getSelectedItem().toString();

        //Validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields required");
            return;
        }

        // Validate password
        String validationMessage = PasswordUtil.getPasswordValidationMessage(password);
        if (validationMessage != null) {
            JOptionPane.showMessageDialog(this, validationMessage);
            return;
        }

        //Update DB
        try {

            //Create user in DB
            User newUser = userDAO.createUser(context.currentUser, username, password, role);

            if (newUser != null) {
                JOptionPane.showMessageDialog(this, "User created successfully");
                context.userRepository.addUser(newUser);
                parent.refreshTable();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create user");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating user");
        }
    }

}
