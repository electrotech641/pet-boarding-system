//Package
package com.petboarding.View;

//Imports
import com.petboarding.Database.UserDAO;
import com.petboarding.Main;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

public class MainScreen extends JFrame {

    private final AppContext context;

    //Data structures
    private final PetRepository petRepository = new PetRepository();
    private final OwnerRepository ownerRepository = new OwnerRepository();
    private final StayRepository stayRepository = new StayRepository();
    private final UserRepository userRepository = new UserRepository();

    //Screens
    private ManageUsersScreen manageUsersScreen;
    private OwnerDetailsScreen ownerDetailsScreen;
    private OwnerSearchResultsScreen ownerSearchResultsScreen;
    private PetSearchResultsScreen petSearchResultsScreen;
    private PetDetailsScreen petDetailsScreen;
    private CreatePetScreen createPetScreen;
    private CreateOwnerScreen createOwnerScreen;



    public MainScreen(User currentUser) throws SQLException {

        context = new AppContext(petRepository, ownerRepository, stayRepository, userRepository);
        context.currentUser = currentUser;
        context.statusLabel = buildStatusLabel();
        context.currentStaysTablePanel = new CurrentStaysTablePanel(context);
        context.petTablePanel = new PetTablePanel(context);
        context.ownerTablePanel = new OwnerTablePanel(context);


        setTitle("Pet Boarding Enhanced System - Logged in as " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = buildTopPanel();
        JSplitPane splitPane = buildMainSplitPane();

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(context.statusLabel, BorderLayout.SOUTH);

        //Load data into table panels
        loadOwnerData();
        loadPetData();
        loadStaysData();
        context.refreshAll();

    }

    /*
        ---------------------UI Builder Methods-------------------------
     */

    //This is where the status lives, at the bottom of the window
    //For now it shows how many pets were loaded
    //and how long a sort operation took
    private JLabel buildStatusLabel() {
        JLabel label = new JLabel("Welcome, " + context.currentUser.getUsername() + "!");
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }

    private JLabel buildLogoPanel() {
        ImageIcon logoIcon = new ImageIcon(Main.class.getResource("/logo.png"));
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        Image scaled = logoIcon.getImage().getScaledInstance(700, 184, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaled));

        return logoLabel;
    }

    //Pet search and add UI elements
    //Add Pet available to Staff and Admin only
    private JPanel buildPetToolsPanel() {
        JPanel petTools = new JPanel(new BorderLayout());
        petTools.setBorder(BorderFactory.createTitledBorder("Pet Tools"));

        /*
            ----------------LEFT PANEL-----------------------
         */
        //Two stacked search rows
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

        /*
            -------------------RIGHT PANEL---------------------------
         */
        //Add Pet button (only for non-read-only users)
        if (!context.currentUser.isReadOnly()) {
            JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton addPetButton = new JButton("Add Pet");
            addPanel.add(addPetButton);
            petTools.add(addPanel, BorderLayout.EAST);

            addPetButton.addActionListener(e -> createPet());
        }

        // Add search stack to center
        petTools.add(searchStack, BorderLayout.CENTER);

        // Actions
        searchByIdButton.addActionListener(e -> petSearchById(Integer.parseInt(petIdField.getText().trim())));
        searchByNameButton.addActionListener(e -> petSearchByName(petNameField.getText().trim()));

        return petTools;
    }



    //Owner search and Add UI
    //Add owner button are Staff and Admin only
    private JPanel buildOwnerToolsPanel() {
        JPanel ownerTools = new JPanel(new BorderLayout());
        ownerTools.setBorder(BorderFactory.createTitledBorder("Owner Tools"));

        JPanel searchStack = new JPanel(new GridLayout(2, 1));
        /*
            ---------LEFT PANEL----------------
         */
        //Row 1
        JPanel searchById = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField ownerIdField = new JTextField(8);
        JButton searchByIdButton = new JButton("Search by ID");
        searchById.add(new JLabel("Owner ID:"));
        searchById.add(ownerIdField);
        searchById.add(searchByIdButton);

        //Row 2
        JPanel searchByName = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField ownerNameField = new JTextField(10);
        JButton searchByNameButton = new JButton("Search by Name");
        searchByName.add(new JLabel("Name:"));
        searchByName.add(ownerNameField);
        searchByName.add(searchByNameButton);

        searchStack.add(searchById);
        searchStack.add(searchByName);

        /*
            ----------------RIGHT PANEL----------------------
         */
        // Add Owner button
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addOwnerButton = new JButton("Add Owner");
        addPanel.add(addOwnerButton);
        ownerTools.add(addPanel, BorderLayout.EAST);

        addOwnerButton.addActionListener(e -> createOwner());

        ownerTools.add(searchStack, BorderLayout.CENTER);

        // Actions
        searchByIdButton.addActionListener(e -> ownerSearchById(Integer.parseInt(ownerIdField.getText().trim())));
        searchByNameButton.addActionListener(e -> ownerSearchByName(ownerNameField.getText().trim()));

        return ownerTools;
    }



    //ADMIN only, this will hold admin only UI elements
    private JPanel buildAdminToolsPanel() {
        if (!context.currentUser.isAdmin()) return null;

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

        // READ_ONLY only show Pet Tools pane
        if (context.currentUser.isReadOnly()) {
            controlBar.setLayout(new GridLayout(1, 1));
            controlBar.add(petTools);
            return controlBar;
        }
        //If STAFF show PET + OWNER tools, including Add buttons
        else if (!context.currentUser.isAdmin()) {
            JPanel ownerTools = buildOwnerToolsPanel();
            controlBar.setLayout(new GridLayout(1, 2));
            controlBar.add(petTools);
            controlBar.add(ownerTools);
            return controlBar;
        } else {
            // ADMIN show all controls
            JPanel adminTools = buildAdminToolsPanel();
            JPanel ownerTools = buildOwnerToolsPanel();
            controlBar.setLayout(new GridLayout(1, 3));
            controlBar.add(petTools);
            controlBar.add(ownerTools);
            controlBar.add(adminTools);

            return controlBar;

        }
    }


    //Build Top Row of the Top Panel
    private JPanel buildStaysTopRow() {
        JPanel row = new JPanel(new BorderLayout());

        row.add(context.currentStaysTablePanel, BorderLayout.WEST);
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
                context.petTablePanel,
                context.ownerTablePanel
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
        context.statusLabel.setText("Loaded " + petRepository.getPetList().size() + " pets from database.");

    }

    public void loadOwnerData() {
        OwnerDAO.loadOwners(ownerRepository);

        if (context.currentUser.isReadOnly()) {
            context.statusLabel.setText("Owner data only accessible to staff and admin");
            return;
        }

    }

    public void loadStaysData() throws SQLException {
        StayDAO.loadCurrentStays(stayRepository);
    }

    /*
        ------------------Search Functions---------------------------
     */
    private void petSearchById(int petId) {
        try {
            Pet pet = petRepository.getPetById(petId);

            if (pet == null) {
                JOptionPane.showMessageDialog(this, "No pet found with ID: " + petId);
                return;
            }

            //If windows is open already, bring it to eh front
            if (petDetailsScreen != null) {
                petDetailsScreen.toFront();
                petDetailsScreen.requestFocus();
                return;
            }

            //Else create a new one
            petDetailsScreen = new PetDetailsScreen(pet, context);

            //when the window closes, set it back to null so it can be reopened later
            petDetailsScreen.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    petDetailsScreen = null;
                }
            });

            petDetailsScreen.setVisible(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void petSearchByName(String name) {
        //Blank name field catch
        if (name == null || name.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please enter a name.");
            return;
        }

        //Get search results
        List<Pet> matches = petRepository.getPetsByName(name);

        //No pets found catch
        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No pets found with name: " + name);
            return;
        }

        //If window alreadyopen, bring it to the front
        if (petSearchResultsScreen != null) {
            petSearchResultsScreen.toFront();
            petSearchResultsScreen.requestFocus();
            return;
        }

        //Otherwise create a new one
        petSearchResultsScreen = new PetSearchResultsScreen(matches, context);

        //When the window closes, set it back to null
        petSearchResultsScreen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                petSearchResultsScreen = null;
            }
        });

        petSearchResultsScreen.setVisible(true);
    }

    private void ownerSearchById(int ownerId) {
        try {
            Owner owner = ownerRepository.getOwnerById(ownerId);

            if (owner == null) {
                JOptionPane.showMessageDialog(this, "No owner found with ID: " + ownerId);
                return;
            }

            //If window already open, bring to front
            if (ownerDetailsScreen != null) {
                ownerDetailsScreen.toFront();
                ownerDetailsScreen.requestFocus();
                return;
            }

            //Otherwise create a new one
            ownerDetailsScreen = new OwnerDetailsScreen(owner, context);

            //When the window closes, allow opening a new one by setting it back to null
            ownerDetailsScreen.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    ownerDetailsScreen = null;
                }
            });

            ownerDetailsScreen.setVisible(true);

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

        //If the window is already open, bring it to the front
        if (ownerSearchResultsScreen != null) {
            ownerSearchResultsScreen.toFront();
            ownerSearchResultsScreen.requestFocus();
            return;
        }

        //Otherwise create a new one
        ownerSearchResultsScreen = new OwnerSearchResultsScreen(matches, context);

        //When the window closes, set it back to null
        ownerSearchResultsScreen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ownerSearchResultsScreen = null;
            }
        });

        ownerSearchResultsScreen.setVisible(true);
    }

    /*
        ----------------------Create Functions-----------------------------------------
     */
    private void createPet() {
        if (createPetScreen != null) {
            createPetScreen.toFront();
            createPetScreen.requestFocus();
            return;
        }

        createPetScreen = new CreatePetScreen(context);

        createPetScreen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                createPetScreen = null;
            }
        });
        createPetScreen.setVisible(true);
    }

    private void createOwner() {
        if (createOwnerScreen != null) {
            createOwnerScreen.toFront();
            createOwnerScreen.requestFocus();
            return;
        }

        createOwnerScreen = new CreateOwnerScreen(context);
        createOwnerScreen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                createOwnerScreen = null;
            }
        });
        createOwnerScreen.setVisible(true);
    }

    /*
        ----------------ADMIN FUNCTIONS------------------
     */
    public void loadUserData() {
        UserDAO.loadUsers(userRepository);

    }

    private void manageUsers() {

        //Ensure only one instance of the window is open
        if (manageUsersScreen != null) {
            manageUsersScreen.toFront();
            manageUsersScreen.requestFocus();
            return;
        }

        manageUsersScreen = new ManageUsersScreen(context);

        manageUsersScreen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                manageUsersScreen = null;
            }
        });

        manageUsersScreen.setVisible(true);

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
