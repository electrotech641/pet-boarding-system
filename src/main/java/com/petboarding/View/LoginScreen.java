//Packages
package com.petboarding.View;

//Imports
import com.petboarding.Database.UserDAO;
import com.petboarding.Models.User;
import com.petboarding.Services.AuthenticationService;
import com.petboarding.Utilities.PasswordUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthenticationService authService = new AuthenticationService();
    private UserDAO userDAO = new UserDAO();

    public LoginScreen() {

        /*
            Set window attributes
         */
        setTitle("Pet Boarding Login");
        setSize(350,200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        /*
            Create new panel and components
         */
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());

        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.addActionListener(e -> handleCreateAccount());

        /*
            Set login button as default, so that ENTER activates it
         */
        getRootPane().setDefaultButton(loginButton);

        /*
            Add components to the window panel
         */
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel(""));
        panel.add(loginButton);
        panel.add(createAccountButton);


        add(panel);
    }

    private void handleLogin() {
        //Get text fields
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        /*
            Attempt to log in using the provided username and password
         */
        try {
            User user = authService.login(username, password);

            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login Successful");

                MainScreen mainView = new MainScreen(user);
                mainView.setVisible(true);
                if (user.isAdmin()) {
                    mainView.loadUserData();
                }
                dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Login Failed");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error");
        }
    }

    private void handleCreateAccount() {
        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required");
            return;
        }

        try {
            //Check for duplicate username BEFORE password validation
            User existing = userDAO.findByUsername(username);
            if (existing != null) {
                JOptionPane.showMessageDialog(this, "That username is already taken. Please choose another.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error");
            return;
        }

        //Password validation, also show password requirements when fails validation check
        String validationMessage = PasswordUtil.getPasswordValidationMessage(password);
        if (validationMessage != null) {
            JOptionPane.showMessageDialog(this, validationMessage);
            return;
        }

        try {
            //Self-registration (read only account) currentUser = null (nobody is logged in)
            User newUser = userDAO.createUser(null, username, password, "READ_ONLY");

            if (newUser != null) {
                JOptionPane.showMessageDialog(this, "Account created successfully");
                handleLogin(); // auto-login
            } else {
                JOptionPane.showMessageDialog(this, "User creation failed");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error");
        }
    }

}
