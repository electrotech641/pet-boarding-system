//Packages
package com.petboarding.View.EditViews;

//Imports
import com.petboarding.Models.Stay;
import com.petboarding.Database.StayDAO;
import com.petboarding.Repository.StayRepository;
import com.petboarding.View.DataViews.CurrentStaysTablePanel;
import com.petboarding.View.DetailViews.StayDetailsScreen;
import javax.swing.*;
import java.awt.*;

public class EditStayScreen extends JFrame {

    private final StayDetailsScreen parent;
    private final StayRepository stayRepository;
    private final CurrentStaysTablePanel currentStaysTablePanel;

    public EditStayScreen(Stay stay, StayDetailsScreen parent, StayRepository stayRepository, CurrentStaysTablePanel currentStaysTablePanel) {
        this.parent = parent;
        this.stayRepository = stayRepository;
        this.currentStaysTablePanel = currentStaysTablePanel;

        setTitle("Edit Stay - #" + stay.getStayId());
        setSize(550, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        // Editable fields
        JTextField checkInField = new JTextField(stay.getCheckInDate());
        JTextField checkOutField = new JTextField(stay.getCheckOutDate());
        JTextField dailyRateField = new JTextField(String.valueOf(stay.getDailyRate()));
        JTextField groomingField = new JTextField(String.valueOf(stay.getGrooming()));

        //Status drop down
        JComboBox<String> statusDropdown = new JComboBox<>(new String[]{"In Progress", "Completed"});
        statusDropdown.setSelectedItem(stay.getStatus());

        // Layout
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

        //Save button functionality, update Stay object and attempt to update DB via DAO
        saveButton.addActionListener(e -> saveStayChanges(stay, checkInField, checkOutField,
                dailyRateField, groomingField, statusDropdown));


        add(panel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }

    private void saveStayChanges(
            Stay stay,
            JTextField checkInField,
            JTextField checkOutField,
            JTextField dailyRateField,
            JTextField groomingField,
            JComboBox<String> statusDropdown
    ) {
        try {
            stay.setCheckInDate(checkInField.getText());
            stay.setCheckOutDate(checkOutField.getText());
            stay.setDailyRate(Double.parseDouble(dailyRateField.getText()));
            stay.setGrooming(Integer.parseInt(groomingField.getText()));
            stay.setStatus(statusDropdown.getSelectedItem().toString());

            // Update stay table in DB
            StayDAO.updateStay(stay);

            boolean completed = stay.getStatus().equals("Completed");

            // Update repository
            if (completed) {
                stayRepository.removeStay(stay.getStayId());
                parent.dispose();   // close StayDetailsScreen if completed
            } else {
                stayRepository.updateStay(stay);
            }

            //Refresh Current Stays table on main screen
            currentStaysTablePanel.loadStaysIntoTable(stayRepository);

            //Refresh details screen if kept open
            if (!completed) {
                parent.refreshDetails();
            }

            //Success message then close edit screen
            JOptionPane.showMessageDialog(this, "Stay updated successfully.");
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating stay: " + ex.getMessage());
        }
    }

}