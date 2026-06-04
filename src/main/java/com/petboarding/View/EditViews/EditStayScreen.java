package com.petboarding.View.EditViews;

import com.petboarding.Models.Stay;
import com.petboarding.Database.StayDAO;
import com.petboarding.View.AppContext;
import com.petboarding.View.DetailViews.StayDetailsScreen;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class EditStayScreen extends JFrame {

    private final StayDetailsScreen parent;
    private final AppContext context;
    private final Stay stay;

    // Editable fields (declared once, used everywhere)
    private JTextField checkInField;
    private JTextField checkOutField;
    private JTextField dailyRateField;
    private JTextField groomingField;
    private JComboBox<String> statusDropdown;

    public EditStayScreen(Stay stay, StayDetailsScreen parent, AppContext context) {
        this.stay = stay;
        this.parent = parent;
        this.context = context;

        setTitle("Edit Stay - #" + stay.getStayId());
        setSize(550, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        //Create fields
        checkInField = new JTextField(stay.getCheckInDate());
        checkOutField = new JTextField(stay.getCheckOutDate());
        dailyRateField = new JTextField(String.valueOf(stay.getDailyRate()));
        groomingField = new JTextField(String.valueOf(stay.getGrooming()));

        statusDropdown = new JComboBox<>(new String[]{"In Progress", "Completed"});
        statusDropdown.setSelectedItem(stay.getStatus());

        //Layout
        panel.add(new JLabel("Check-In:"));
        panel.add(checkInField);

        panel.add(new JLabel("Check-Out:"));
        panel.add(checkOutField);

        panel.add(new JLabel("Daily Rate:"));
        panel.add(dailyRateField);

        panel.add(new JLabel("Grooming (0=NO, 1=YES):"));
        panel.add(groomingField);

        panel.add(new JLabel("Status (Completed/In Progress):"));
        panel.add(statusDropdown);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> saveStayChanges());

        add(panel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }

    private void saveStayChanges() {
        try {
            // Update stay object
            stay.setCheckInDate(checkInField.getText());
            stay.setCheckOutDate(checkOutField.getText());
            stay.setDailyRate(Double.parseDouble(dailyRateField.getText()));
            stay.setGrooming(Integer.parseInt(groomingField.getText()));
            stay.setStatus(statusDropdown.getSelectedItem().toString());

            // Update DB
            StayDAO.updateStay(stay);

            boolean completed = stay.getStatus().equalsIgnoreCase("Completed");
            boolean nowInProgress = stay.getStatus().equalsIgnoreCase("In Progress");

            //completed, remove from repo
            if (completed) {
                context.stayRepository.removeStay(stay.getStayId());
            }

            // CASE 2: In Progress → ensure it is in repository
            if (nowInProgress) {
                if (!context.stayRepository.containsStay(stay.getStayId())) {
                    context.stayRepository.addStay(stay);
                } else {
                    context.stayRepository.updateStay(stay);
                }
            }

            //Refresh screens
            context.refreshCurrentStays();
            parent.refreshDetails();

            JOptionPane.showMessageDialog(this, "Stay updated successfully.");
            dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating stay: " + e.getMessage());
        }
    }
}
