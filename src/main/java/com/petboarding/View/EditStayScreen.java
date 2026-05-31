//Packages
package com.petboarding.View;

//Imports
import com.petboarding.Models.Stay;
import com.petboarding.Database.StayDAO;
import com.petboarding.View.DetailViews.StayDetailsScreen;
import javax.swing.*;
import java.awt.*;

public class EditStayScreen extends JFrame {

    private StayDetailsScreen parent;

    public EditStayScreen(Stay stay, StayDetailsScreen parent) {
        this.parent = parent;

        setTitle("Edit Stay - #" + stay.getStayId());
        setSize(550, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        // Editable fields
        JTextField checkInField = new JTextField(stay.getCheckInDate());
        JTextField checkOutField = new JTextField(stay.getCheckOutDate());
        JTextField dailyRateField = new JTextField(String.valueOf(stay.getDailyRate()));
        JTextField groomingField = new JTextField(String.valueOf(stay.getGrooming()));
        JTextField statusField = new JTextField(stay.getStatus());

        // Layout
        panel.add(new JLabel("Check-In:"));
        panel.add(checkInField);

        panel.add(new JLabel("Check-Out:"));
        panel.add(checkOutField);

        panel.add(new JLabel("Daily Rate:"));
        panel.add(dailyRateField);

        panel.add(new JLabel("Grooming (0=NO, 1=YES):"));
        panel.add(groomingField);

        panel.add(new JLabel("Status (Complete/In Progress):"));
        panel.add(statusField);

        JButton saveButton = new JButton("Save Changes");

        //Save button functionality, update Stay object and attempt to update DB via DAO
        saveButton.addActionListener(e -> {
            try {
                stay.setCheckInDate(checkInField.getText());
                stay.setCheckOutDate(checkOutField.getText());
                stay.setDailyRate(Double.parseDouble(dailyRateField.getText()));
                stay.setGrooming(Integer.parseInt(groomingField.getText()));
                stay.setStatus(statusField.getText());

                StayDAO.updateStay(stay);

                //Refresh parent screen
                parent.refreshDetails();

                JOptionPane.showMessageDialog(this, "Stay updated successfully.");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating stay: " + ex.getMessage());
            }
        });

        add(panel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }
}