/*
 * Pet Boarding Application Enhanced
 * Created by Derick Silva for SNHU Capstone
 *
 * This started as a school project that only consisted of a console window that prompted the user for pet information
 * It had no persistent data, no check-in/check-out functionality, no database integration, and no role based security
 * It simply prompted for information about a pet, and then closed.
 *
 * The enhanced version is complete with a graphical user interface, SQLite database integration,
 * role-based security and all the following functions:
 *
 * Full stack pet boarding management system, complete with Check-In, Check-Out functionality
 * READ_ONLY users can view pets and current stays, but not edit, check-in or check-out
 * STAFF users can View pet, owner, current stay information. They can also edit and add pets and owners as well as check-in or check-out pets
 * ADMIN users can do all of the above and manage users, including deleting, editing, and creating
 *
 * CREATE USER button on login screen will create a READ_ONLY user with the username and password typed into the login fields
 *
 *
 * -------Sample data is loaded below using the CSVLoader class, only run that once if sample data to test with is needed.-----------
 */



//Package
package com.petboarding;

//Imports
import com.petboarding.Database.DatabaseManager;
import com.petboarding.Database.UserDAO;
import com.petboarding.Database.CSVLoader;
import com.petboarding.Models.User;
import com.petboarding.View.LoginScreen;

public class Main {

    //CHANGE THESE BEFORE INITIAL BOOT FOR DEFAULT ADMIN ACCOUNT
    private static final String defaultAdmin = "admin";
    private static final String defaultAdminPw = "admin";

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
            Ensures there is a default admin account
            Remove if not needed
            username = admin password = admin
         */
        createDefaultAdminUser();

        /*
            Load sample data, run once to load database
            Drop tables owners, pets, stays before re-loading data
            to avoid duplicate data and non-unique id errors
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

        try {
            User existing = userDAO.findByUsername("admin");

            if (existing == null) {
                System.out.println("No admin user found — creating default admin user");

                userDAO.createDefaultAdminUser("admin", "admin");

                System.out.println("Created default admin: username= " + defaultAdmin + " password= " + defaultAdminPw);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to create default admin user");
        }
    }

}