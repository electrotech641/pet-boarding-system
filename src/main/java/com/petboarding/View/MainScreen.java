//Packages
package com.petboarding.View;

//Imports
import com.petboarding.Data.CurrentStays;
import com.petboarding.Data.OwnerData;
import com.petboarding.Data.PetData;
import com.petboarding.Database.CurrentStaysRepository;
import com.petboarding.Database.OwnerRepository;
import com.petboarding.Database.PetRepository;
import com.petboarding.Models.*;
import javax.swing.*;
import java.awt.*;

public class MainScreen extends JFrame {

    private User currentUser;
    private JLabel statusLabel;
    private PetData petData = new PetData();
    private PetTablePanel petTablePanel;
    private OwnerData ownerData = new OwnerData();
    private OwnerTablePanel ownerTablePanel;
    private CurrentStaysTablePanel currentStaysTablePanel;
    private CurrentStays currentStaysData = new CurrentStays();

    public MainScreen(User user) {
        this.currentUser = user;

        //Set window attributes
        setTitle("Pet Boarding Enhanced System - Logged in as " + user.getUsername() + " (" + user.getRole() + ")");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Create status label
        statusLabel = new JLabel("Welcome, " + user.getUsername() + "!");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        //Load logo image
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/logo.png"));
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);

        //Scale logo image
        Image scaled = logoIcon.getImage().getScaledInstance(700, 184, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaled));

        //Add pet table and owners table split panel
        petTablePanel = new PetTablePanel(petData, ownerData, user, statusLabel);
        ownerTablePanel = new OwnerTablePanel(ownerData, user, statusLabel);
        currentStaysTablePanel = new CurrentStaysTablePanel(currentStaysData, currentUser, statusLabel);

        // Manage Stays panel (right side of stays table)
        JPanel manageStaysPanel = new JPanel();
        manageStaysPanel.setLayout(new BoxLayout(manageStaysPanel, BoxLayout.Y_AXIS));
        manageStaysPanel.setBorder(BorderFactory.createTitledBorder("Manage Stays"));

        JButton newStayButton = new JButton("Create New Stay");
        JButton checkOutButton = new JButton("Check-Out Stay");

        newStayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkOutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        manageStaysPanel.add(Box.createVerticalStrut(10));
        manageStaysPanel.add(newStayButton);
        manageStaysPanel.add(Box.createVerticalStrut(10));
        manageStaysPanel.add(checkOutButton);
        manageStaysPanel.add(Box.createVerticalStrut(10));

        //Build pet lookup and add pet panel
        JPanel petTools = new JPanel(new BorderLayout());
        petTools.setBorder(BorderFactory.createTitledBorder("Pet Tools"));

        // LEFT SIDE: Search controls
        JPanel petSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField petSearchField = new JTextField(8);
        JButton petSearchButton = new JButton("Search Pet");

        petSearchPanel.add(new JLabel("Pet ID:"));
        petSearchPanel.add(petSearchField);
        petSearchPanel.add(petSearchButton);

        // RIGHT SIDE: Add Pet button
        JButton addPetButton = new JButton("Add Pet");
        JPanel addPetPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addPetPanel.add(addPetButton);

        // Add both to the pet tools panel
        petTools.add(petSearchPanel, BorderLayout.WEST);
        petTools.add(addPetPanel,BorderLayout.EAST);

        //Build owner lookup and add panel
        JPanel ownerTools = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ownerTools.setBorder(BorderFactory.createTitledBorder("Owner Tools"));

        JTextField ownerSearchField = new JTextField(8);
        JButton ownerSearchButton = new JButton("Search Owner");
        JButton addOwnerButton = new JButton("Add Owner");

        ownerTools.add(new JLabel("Owner ID:"));
        ownerTools.add(ownerSearchField);
        ownerTools.add(ownerSearchButton);
        ownerTools.add(addOwnerButton);

        //Admin ONLY panel
        JPanel adminTools = new JPanel(new FlowLayout(FlowLayout.LEFT));
        adminTools.setBorder(BorderFactory.createTitledBorder("Admin Tools"));

        if (currentUser.isAdmin()) {
            JButton manageUsersButton = new JButton("Manage Users");
            adminTools.add(manageUsersButton);
        }



        //Build control bar with owner, pet, and admin tools
        JPanel controlBar = new JPanel(new GridLayout(1, 3));
        controlBar.add(petTools);
        controlBar.add(ownerTools);
        controlBar.add(adminTools);

        //Build the top row of the top panel with stays and stays management
        JPanel staysTopRow = new JPanel(new BorderLayout());

        // Left side: stays table
        staysTopRow.add(currentStaysTablePanel, BorderLayout.WEST);

        //Center logo
        staysTopRow.add(logoLabel, BorderLayout.CENTER);

        // Right side: manage stays box
        staysTopRow.add(manageStaysPanel, BorderLayout.EAST);

        //Construct top panel with stays and stay management on top and pet/owner and admin tools across the middle
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(staysTopRow, BorderLayout.NORTH);   // stays table + manage stays
        topPanel.add(controlBar, BorderLayout.SOUTH);    // pet/owner/admin tools



        //Search button functionality
        petSearchButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(petSearchField.getText().trim());

                // Use your HashMap lookup (fastest)
                Pet pet = petData.getPetById(id);

                if (pet != null) {
                    new PetDetailsScreen(pet, currentUser, ownerData).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "No pet found with ID: " + id);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
            }
        });


        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                petTablePanel,
                ownerTablePanel
        );

        splitPane.setDividerLocation(900);

        /*
            Construct screen layout
         */
        add(topPanel,BorderLayout.NORTH);

        add(splitPane, BorderLayout.CENTER);

        //Add label to bottom of screen
        add(statusLabel, BorderLayout.SOUTH);


    }

    //Load pets from database into memory
    public void loadPetData() {
        PetRepository.loadPets(petData);

        int count = petData.getPetList().size();
        statusLabel.setText("Loaded " + count + " pets from database.");

        petTablePanel.loadPetsIntoTable(petData);
    }

    public void loadOwnerData() {
        OwnerRepository.loadOwners(ownerData);
        ownerTablePanel.loadOwnersIntoTable(ownerData);
    }

    public void loadStaysData() {
        CurrentStaysRepository.loadCurrentStays(currentStaysData);
        currentStaysTablePanel.loadStaysIntoTable(currentStaysData);
    }
}
