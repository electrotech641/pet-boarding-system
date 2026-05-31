package com.petboarding;

import com.petboarding.Database.DatabaseManager;
import com.petboarding.Database.UserDAO;
import com.petboarding.Database.CSVLoader;
import com.petboarding.Models.User;
import com.petboarding.View.LoginScreen;

public class Main {

    public static void main(String[] args) {

        /*
            Get database connection from DatabaseManager class
         */
        try {
            DatabaseManager.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to initialize database");
            return;
        }

        /*
            If none exists, create default admin user
            *************REMOVE IF PLANNED TO SHIP***************************
            Implement alternative controls for admin user creation to prevent
            unauthorized access
         */
        createDefaultAdminUser();

        /*
            Load sample data, run once to load database
         */

        //CSVLoader.loadOwners("owners_sample_data.csv");
        //CSVLoader.loadPets("pets_sample_data.csv");
        //CSVLoader.loadStays("stays_sample_data.csv");

        //Display LOGIN screen on startup, ensure Swing does this on the main thread to avoid UI issues
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }

    private static void createDefaultAdminUser() {
        UserDAO userDAO = new UserDAO();

        /*
            Search for existing "admin" user, if none exists, creates it with default password

         */
        try {
            User existing = userDAO.findByUsername("admin");

            if (existing == null) {
                System.out.println("No admin user found - creating default admin user");

                userDAO.createUser("admin", "admin", "ADMIN");   //Change this to desired admin password

                System.out.println("Created default admin, username: admin, password: admin");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to create default admin user");
        }
    }
}