//Package
package com.petboarding.View;

//Imports
import com.petboarding.Database.UserDAO;
import com.petboarding.Repository.StayRepository;
import com.petboarding.Repository.OwnerRepository;
import com.petboarding.Repository.PetRepository;
import com.petboarding.Database.StayDAO;
import com.petboarding.Database.OwnerDAO;
import com.petboarding.Database.PetDAO;
import com.petboarding.Models.*;
import com.petboarding.Repository.UserRepository;
import com.petboarding.View.CreateViews.CreateOwnerScreen;
import com.petboarding.View.DataViews.CurrentStaysTablePanel;
import com.petboarding.View.DataViews.OwnerTablePanel;
import com.petboarding.View.DataViews.PetTablePanel;
import com.petboarding.View.CreateViews.CreatePetScreen;
import com.petboarding.View.DetailViews.OwnerDetailsScreen;
import com.petboarding.View.DetailViews.PetDetailsScreen;
import com.petboarding.View.SearchResultsViews.OwnerSearchResultsScreen;
import com.petboarding.View.SearchResultsViews.PetSearchResultsScreen;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MainScreen extends JFrame {

    private final User currentUser;
    private final JLabel statusLabel;

    //Data structures
    private final PetRepository petRepository = new PetRepository();
    private final OwnerRepository ownerRepository = new OwnerRepository();
    private final StayRepository stayRepository = new StayRepository();
    private final UserRepository userRepository = new UserRepository();

    //UI panels
    private final PetTablePanel petTablePanel;
    private final OwnerTablePanel ownerTablePanel;
    private final CurrentStaysTablePanel currentStaysTablePanel;

    public MainScreen(User currentUser) {
        this.currentUser = currentUser;

        setTitle("Pet Boarding Enhanced System - Logged in as " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        statusLabel = buildStatusLabel();
        currentStaysTablePanel = new CurrentStaysTablePanel(stayRepository, petRepository, ownerRepository, this.currentUser, statusLabel);
        petTablePanel = new PetTablePanel(petRepository, ownerRepository, stayRepository, currentStaysTablePanel, currentUser, statusLabel);
        ownerTablePanel = new OwnerTablePanel(ownerRepository, currentUser, statusLabel);


        JPanel topPanel = buildTopPanel();
        JSplitPane splitPane = buildMainSplitPane();

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    /*
        ---------------------UI Builder Methods-------------------------
     */

    //This is where the status lives, at the bottom of the window
    //For now it shows how many pets were loaded
    //and how long a sort operation took
    private JLabel buildStatusLabel() {
        JLabel label = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }

    private JLabel buildLogoPanel() {
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/logo.png"));
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        Image scaled = logoIcon.getImage().getScaledInstance(700, 184, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaled));

        return logoLabel;
    }

    //Pet search and add UI elements
    //Search available to READ_ONLY, add for STAFF and above
    private JPanel buildPetToolsPanel() {
        JPanel petTools = new JPanel(new BorderLayout());
        petTools.setBorder(BorderFactory.createTitledBorder("Pet Tools"));

        // LEFT SIDE: Two stacked search rows
        JPanel searchStack = new JPanel(new GridLayout(2, 1));

        // Row 1: Search by ID
        JPanel searchById = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField petIdField = new JTextField(8);
        JButton searchByIdButton = new JButton("Search by ID");
        searchById.add(new JLabel("Pet ID:"));
        searchById.add(petIdField);
        searchById.add(searchByIdButton);

        // Row 2: Search by Name
        JPanel searchByName = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField petNameField = new JTextField(10);
        JButton searchByNameButton = new JButton("Search by Name");
        searchByName.add(new JLabel("Name:"));
        searchByName.add(petNameField);
        searchByName.add(searchByNameButton);

        searchStack.add(searchById);
        searchStack.add(searchByName);

        // RIGHT SIDE: Add Pet button
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addPetButton = new JButton("Add Pet");
        addPanel.add(addPetButton);

        // Add to main panel
        petTools.add(searchStack, BorderLayout.CENTER);
        petTools.add(addPanel, BorderLayout.EAST);

        // Actions
        searchByIdButton.addActionListener(e -> petSearchById(Integer.parseInt(petIdField.getText().trim())));
        searchByNameButton.addActionListener(e -> petSearchByName(petNameField.getText().trim()));
        addPetButton.addActionListener(e -> createPet());

        return petTools;
    }


    //Owner search and Add UI
    //This will be staff only access
    private JPanel buildOwnerToolsPanel() {
        JPanel ownerTools = new JPanel(new BorderLayout());
        ownerTools.setBorder(BorderFactory.createTitledBorder("Owner Tools"));

        // Create panel for search functions
        JPanel searchStack = new JPanel(new GridLayout(2, 1));

        // Row 1, search by ID, Label - Field - Button
        JPanel searchById = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField ownerIdField = new JTextField(8);
        JButton searchByIdButton = new JButton("Search by ID");
        searchById.add(new JLabel("Owner ID:"));
        searchById.add(ownerIdField);
        searchById.add(searchByIdButton);

        // Row 2, search by name, Label - Field - Button
        JPanel searchByName = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField ownerNameField = new JTextField(10);
        JButton searchByNameButton = new JButton("Search by Name");
        searchByName.add(new JLabel("Name:"));
        searchByName.add(ownerNameField);
        searchByName.add(searchByNameButton);

        //Build search panel
        searchStack.add(searchById);
        searchStack.add(searchByName);

        //Add owner button, to the far right
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addOwnerButton = new JButton("Add Owner");
        addPanel.add(addOwnerButton);

        // Add both panels to owner tools
        ownerTools.add(searchStack, BorderLayout.CENTER);
        ownerTools.add(addPanel, BorderLayout.EAST);

        // Actions
        searchByIdButton.addActionListener(e -> ownerSearchById(Integer.parseInt(ownerIdField.getText().trim())));
        searchByNameButton.addActionListener(e -> ownerSearchByName(ownerNameField.getText().trim()));
        addOwnerButton.addActionListener(e -> createOwner());

        return ownerTools;
    }


    //ADMIN only, this will hold admin only UI elements
    private JPanel buildAdminToolsPanel() {
        if (!currentUser.isAdmin()) return null;

        JPanel adminTools = new JPanel(new FlowLayout(FlowLayout.LEFT));
        adminTools.setBorder(BorderFactory.createTitledBorder("Admin Tools"));

        JButton manageUsersButton = new JButton("Manage Users");
        adminTools.add(manageUsersButton);

        manageUsersButton.addActionListener(e -> manageUsers());

        return adminTools;
    }

    //This is the bottom row of the Top Panel
    //where pet tools, owner tools, and ADMIN tools live
    private JPanel buildControlBar() {
        JPanel controlBar = new JPanel();

        JPanel petTools = buildPetToolsPanel();
        JPanel ownerTools = buildOwnerToolsPanel();
        JPanel adminTools = buildAdminToolsPanel();

        if (currentUser.isAdmin()) {
            controlBar.setLayout(new GridLayout(1, 3));
            controlBar.add(petTools);
            controlBar.add(ownerTools);
            controlBar.add(adminTools);
        } else {
            controlBar.setLayout(new GridLayout(1, 2));
            controlBar.add(petTools);
            controlBar.add(ownerTools);
        }

        return controlBar;
    }

    //Build Top Row of the Top Panel
    private JPanel buildStaysTopRow() {
        JPanel row = new JPanel(new BorderLayout());

        row.add(currentStaysTablePanel, BorderLayout.WEST);
        row.add(buildLogoPanel(), BorderLayout.CENTER);

        // Add logout button to the right of the logo
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(buildLogoutButton());
        row.add(logoutPanel, BorderLayout.EAST);

        return row;
    }


    //Build the Top Panel, includes Current Stays and Control Bar (tools)
    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        top.add(buildStaysTopRow(), BorderLayout.NORTH);
        top.add(buildControlBar(), BorderLayout.SOUTH);

        return top;
    }


    //Split pane holds Pet and Owner data
    private JSplitPane buildMainSplitPane() {
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                petTablePanel,
                ownerTablePanel
        );
        splitPane.setDividerLocation(900);
        return splitPane;
    }

    private JButton buildLogoutButton() {
        JButton logoutButton = new JButton("Logout");

        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                logout();
            }
        });

        return logoutButton;
    }

    /*
        ------------Data Methods-------------------------------------
     */

    public void loadPetData() throws SQLException {
        PetDAO.loadPets(petRepository);
        statusLabel.setText("Loaded " + petRepository.getPetList().size() + " pets from database.");
        petTablePanel.loadPetsIntoTable(petRepository);
    }

    public void loadOwnerData() {
        OwnerDAO.loadOwners(ownerRepository);

        if (currentUser.isReadOnly()) {
            statusLabel.setText("Owner data only accessible to staff and admin");
            return;
        }

        ownerTablePanel.loadOwnersIntoTable(ownerRepository);
    }

    public void loadStaysData() throws SQLException {
        StayDAO.loadCurrentStays(stayRepository);
        currentStaysTablePanel.loadStaysIntoTable(stayRepository);
    }

    /*
        ------------------Search Functions---------------------------
     */
    private void petSearchById(int petId) {
        try {
            Pet pet = petRepository.getPetById(petId);

            if (pet != null) {
                new PetDetailsScreen(pet, currentUser, ownerRepository, stayRepository, currentStaysTablePanel, petRepository).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "No pet found with ID: " + petId);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void petSearchByName(String name) {
        if (name == null || name.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please enter a name.");
            return;
        }

        List<Pet> matches = petRepository.getPetsByName(name);

        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No pets found with name: " + name);
            return;
        }

        new PetSearchResultsScreen(matches, currentUser, ownerRepository, stayRepository, currentStaysTablePanel, petRepository).setVisible(true);
    }

    private void ownerSearchById(int ownerId) {
        try {
            Owner owner = ownerRepository.getOwnerById(ownerId);

            if (owner != null) {
                new OwnerDetailsScreen(owner, currentUser).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "No owner found with ID: " + ownerId);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        }
    }

    private void ownerSearchByName(String name) {
        if (name == null || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name.");
            return;
        }

        List<Owner> matches = ownerRepository.getOwnersByName(name);

        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No owner found with name: " + name);
            return;
        }

        new OwnerSearchResultsScreen(matches, currentUser).setVisible(true);
    }

    /*
        ----------------------Create Functions-----------------------------------------
     */
    private void createPet() {
        new CreatePetScreen(petRepository, ownerRepository, petTablePanel).setVisible(true);
    }

    private void createOwner() {
        new CreateOwnerScreen(ownerRepository, ownerTablePanel).setVisible(true);
    }

    /*
        ----------------ADMIN FUNCTIONS------------------
     */
    private void manageUsers() {
        UserDAO.loadUsers(userRepository);
        new ManageUsersScreen(userRepository, currentUser, statusLabel).setVisible(true);
    }

    /*
        ------------MISC----------------
     */

    private void logout() {
        // Dispose ALL open windows except the LoginScreen we are about to create
        for (Window window : Window.getWindows()) {
            if (window instanceof JFrame || window instanceof JDialog) {
                window.dispose();
            }
        }

        // Return to log in screen
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}
