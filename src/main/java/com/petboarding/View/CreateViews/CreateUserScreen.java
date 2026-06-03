package com.petboarding.View.CreateViews;

import com.petboarding.Database.UserDAO;
import com.petboarding.Models.User;
import com.petboarding.Repository.UserRepository;
import com.petboarding.Utilities.PasswordUtil;
import com.petboarding.View.ManageUsersScreen;

import javax.swing.*;
import java.awt.*;

public class CreateUserScreen extends JFrame {

    private UserDAO userDAO = new UserDAO();
    private final UserRepository userRepository;
    private final ManageUsersScreen parent;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleDropdown;
    private JButton createButton;

    public CreateUserScreen(UserRepository userRepository, ManageUsersScreen parent) {
        this.userRepository = userRepository;
        this.parent = parent;

        setTitle("Create User");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        roleDropdown = new JComboBox<>(new String[]{"ADMIN", "READ_ONLY", "STAFF"});

        createButton = new JButton("Create");
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

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields required");
            return;
        }

        //Enforce password rules before hashing, return if validation failed
        if (!isPasswordValid(password)) {
            return;
        }

        try {

            //Create user in DB
            User newUser = userDAO.createUser(username, password, role);

            if (newUser != null) {
                JOptionPane.showMessageDialog(this, "User created successfully");
                userRepository.addUser(newUser);
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


    private boolean isPasswordValid(String password) {
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long");
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            JOptionPane.showMessageDialog(this, "Password must contain at least one uppercase letter");
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            JOptionPane.showMessageDialog(this, "Password must contain at least one lowercase letter");
            return false;
        }
        if (!password.matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(this, "Password must contain at least one number");
            return false;
        }
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            JOptionPane.showMessageDialog(this, "Password must contain at least one special character");
            return false;
        }

        return true;
    }

}
