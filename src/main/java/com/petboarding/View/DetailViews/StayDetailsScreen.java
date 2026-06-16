package com.petboarding.View.DetailViews;

import com.petboarding.Models.Stay;
import com.petboarding.Database.StayDAO;
import com.petboarding.Models.Pet;
import com.petboarding.Models.Owner;
import com.petboarding.View.AppContext;
import com.petboarding.View.EditViews.EditStayScreen;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

public class StayDetailsScreen extends JFrame {

    private final AppContext context;
    private Stay stay;

    //Buttons and labels
    private JButton checkOutButton, editStayButton, petDetailsButton, ownerDetailsButton;
    private JLabel checkInLabel, checkOutLabel, petLabel, ownerLabel;

    //Child screens
    private EditStayScreen editStayScreen;
    private PetDetailsScreen petDetailsScreen;
    private OwnerDetailsScreen ownerDetailsScreen;

    public StayDetailsScreen(Stay stay, AppContext context) {
        this.stay = stay;
        this.context = context;

        setTitle("Stay Details - Stay #" + stay.getStayId());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //Load pet and owner
        Pet pet = context.petRepository.getPetById(stay.getPetId());
        Owner owner = context.ownerRepository.getOwnerById(pet.getOwnerId());

        //Create label
        petLabel = new JLabel("Pet: " + pet.getName());
        ownerLabel = new JLabel("Owner: " + owner.getName());
        checkInLabel = new JLabel("Check-In: " + stay.getCheckInDate());
        checkOutLabel = new JLabel("Check-Out: " + stay.getCheckOutDate());

        //create Pet Details button
        petDetailsButton = new JButton("Pet Details");
        petDetailsButton.addActionListener(e -> {
            try {
                openPetDetailsScreen(pet);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        //Layout
        panel.add(new JLabel("Stay ID: " + stay.getStayId()));
        panel.add(petLabel);
        panel.add(ownerLabel);
        panel.add(checkInLabel);
        panel.add(checkOutLabel);
        panel.add(new JLabel(" "));
        panel.add(Box.createVerticalStrut(10));
        panel.add(petDetailsButton);

        // Admin/Staff buttons
        boolean canManage = context.currentUser.isAdmin() || context.currentUser.isStaff();

        if (canManage) {

            //Edit Stay button
            editStayButton = new JButton("Edit Stay");
            editStayButton.addActionListener(e -> openEditStayScreen());
            panel.add(Box.createVerticalStrut(10));
            panel.add(editStayButton);

            //Check-Out button
            checkOutButton = new JButton("Check-Out");
            checkOutButton.addActionListener(e -> {
                try {
                    handleCheckOut(pet);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });

            if (stay.getStatus().equalsIgnoreCase("In Progress")) {
                panel.add(Box.createVerticalStrut(10));
                panel.add(checkOutButton);
            }

            // Owner Details
            ownerDetailsButton = new JButton("Owner Details");
            ownerDetailsButton.addActionListener(e -> openOwnerDetailsScreen(owner));
            panel.add(Box.createVerticalStrut(10));
            panel.add(ownerDetailsButton);
        }

        add(panel);
    }

    public void loadStay(Stay stay) throws SQLException {
        this.stay = stay;
        refreshDetails();
    }

    public void refreshDetails() {
        try {
            //If stay is null, clear UI and hide all buttons
            if (stay == null) {
                setTitle("Stay Details");

                petLabel.setText("Pet: N/A");
                ownerLabel.setText("Owner: N/A");
                checkInLabel.setText("Check-In: N/A");
                checkOutLabel.setText("Check-Out: N/A");

                if (checkOutButton != null) checkOutButton.setVisible(false);
                if (editStayButton != null) editStayButton.setVisible(false);
                if (petDetailsButton != null) petDetailsButton.setVisible(false);
                if (ownerDetailsButton != null) ownerDetailsButton.setVisible(false);

                revalidate();
                repaint();
                return;
            }

            //Reload pet
            Pet pet = context.petRepository.getPetById(stay.getPetId());

            //Reload owner if pet exists
            Owner owner = null;
            if (pet != null) {
                petLabel.setText("Pet: " + pet.getName());
                int ownerId = pet.getOwnerId();
                owner = context.ownerRepository.getOwnerById(ownerId);
            } else {
                petLabel.setText("Pet: Unknown");
            }

            //Update title
            setTitle("Stay Details - Stay #" + stay.getStayId());

            //Update owner label
            if (owner != null) {
                ownerLabel.setText("Owner: " + owner.getName());
            } else {
                ownerLabel.setText("Owner: Unknown");
            }

            //Update check-in/out labels
            checkInLabel.setText("Check-In: " + stay.getCheckInDate());
            checkOutLabel.setText("Check-Out: " + stay.getCheckOutDate());

            //Update Check-Out button visibility
            boolean canManage = context.currentUser.isAdmin() || context.currentUser.isStaff();
            boolean notCheckedOut = stay.getStatus().equalsIgnoreCase("In Progress");

            if (checkOutButton != null) {
                checkOutButton.setVisible(canManage && notCheckedOut);
            }

            //Show edit stay and owner details is user is non-READ_ONLY
            if (editStayButton != null) editStayButton.setVisible(canManage);
            if (ownerDetailsButton != null) ownerDetailsButton.setVisible(canManage);

            //Pet Details is always visible
            if (petDetailsButton != null) petDetailsButton.setVisible(true);

            revalidate();
            repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleCheckOut(Pet pet) throws SQLException {
        if (stay.getStatus().equalsIgnoreCase("Completed")) {
            JOptionPane.showMessageDialog(this, "This stay is already checked out.",
                    "Already Completed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Are you sure you want to check-out this stay?",
                "Confirm Check-Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        String today = java.time.LocalDate.now().toString();
        stay.setCheckOutDate(today);
        stay.setStatus("Completed");

        StayDAO.updateStay(stay);
        refreshDetails();

        if (checkOutButton != null) checkOutButton.setVisible(false);

        context.stayRepository.removeStay(stay.getStayId());
        context.refreshCurrentStays();

        JOptionPane.showMessageDialog(this,
                pet.getName() + " checked out successfully!",
                "Check-Out Complete",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void openEditStayScreen() {
        if (editStayScreen != null) {
            editStayScreen.toFront();
            editStayScreen.requestFocus();
            return;
        }

        editStayScreen = new EditStayScreen(stay, this, context);
        editStayScreen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                editStayScreen = null;
            }
        });

        editStayScreen.setVisible(true);
    }

    private void openPetDetailsScreen(Pet pet) throws SQLException {
        if (petDetailsScreen != null) {
            petDetailsScreen.toFront();
            petDetailsScreen.requestFocus();
            return;
        }

        petDetailsScreen = new PetDetailsScreen(pet, context);
        petDetailsScreen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                petDetailsScreen = null;
            }
        });

        petDetailsScreen.setVisible(true);
    }

    private void openOwnerDetailsScreen(Owner owner) {
        if (ownerDetailsScreen != null) {
            ownerDetailsScreen.toFront();
            ownerDetailsScreen.requestFocus();
            return;
        }

        ownerDetailsScreen = new OwnerDetailsScreen(owner, context);
        ownerDetailsScreen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                ownerDetailsScreen = null;
            }
        });

        ownerDetailsScreen.setVisible(true);
    }
}
