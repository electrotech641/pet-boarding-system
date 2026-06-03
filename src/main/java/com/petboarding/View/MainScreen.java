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
import java.util.List;

public class MainScreen extends JFrame {

    private User currentUser;
    private JLabel statusLabel;

    //Data structures
    private PetRepository petRepository = new PetRepository();
    private OwnerRepository ownerRepository = new OwnerRepository();
    private StayRepository stayRepository = new StayRepository();
    private UserRepository userRepository = new UserRepository();

    //UI panels
    private PetTablePanel petTablePanel;
    private OwnerTablePanel ownerTablePanel;
    private CurrentStaysTablePanel currentStaysTablePanel;

    public MainScreen(User user) {
        this.currentUser = user;

        setTitle("Pet Boarding Enhanced System - Logged in as " + user.getUsername() + " (" + user.getRole() + ")");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        statusLabel = buildStatusLabel();
        petTablePanel = new PetTablePanel(petRepository, ownerRepository, user, statusLabel);
        ownerTablePanel = new OwnerTablePanel(ownerRepository, user, statusLabel);
        currentStaysTablePanel = new CurrentStaysTablePanel(stayRepository, petRepository, ownerRepository, currentUser, statusLabel);

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

    /*
        ------------Data Methods-------------------------------------
     */

    public void loadPetData() {
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

    public void loadStaysData() {
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
                new PetDetailsScreen(pet, currentUser, ownerRepository).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "No pet found with ID: " + petId);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
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

        new PetSearchResultsScreen(matches, currentUser, ownerRepository).setVisible(true);
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
        new ManageUsersScreen(userRepository, currentUser).setVisible(true);
    }
}
