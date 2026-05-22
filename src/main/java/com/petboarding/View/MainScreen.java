//Packages
package com.petboarding.View;

//Imports
import com.petboarding.Models.User;
import com.petboarding.Services.AuthenticationService;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainScreen extends JFrame {

    private User currentUser;

    public MainScreen(User user) {
        this.currentUser = user;

        //Set window attributes
        setTitle("Pet Boarding Enhanced System - Logged in as " + user.getUsername() + " (" + user.getRole() + ")");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
