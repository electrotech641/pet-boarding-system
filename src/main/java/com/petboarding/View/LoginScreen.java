//Packages
package com.petboarding.View;

//Imports
import com.petboarding.Models.User;
import com.petboarding.Services.AuthenticationService;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthenticationService authService = new AuthenticationService();

    public LoginScreen() {

        /*
            Set window attributes
         */
        setTitle("Pet Boarding Login");
        setSize(350,200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        /*
            Create new panel and compenents
         */
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());

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


        add(panel);
    }

    private void handleLogin() {
        //Get text fields
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        /*
            Attempt to login using the provided username and password
         */
        try {
            User user = authService.login(username, password);

            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login Successful");

                new MainScreen(user).setVisible(true);
                dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Login Failed");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error");
        }
    }
}
