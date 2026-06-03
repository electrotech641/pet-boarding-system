package com.petboarding.View.CreateViews;

import com.petboarding.Models.Stay;
import com.petboarding.Models.Pet;
import com.petboarding.Models.User;
import com.petboarding.Database.StayDAO;
import com.petboarding.Repository.StayRepository;
import com.petboarding.View.DataViews.CurrentStaysTablePanel;
import com.petboarding.View.DetailViews.PetDetailsScreen;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class CheckInStayScreen extends JFrame {

    private final StayRepository stayRepository;
    private final CurrentStaysTablePanel currentStaysTablePanel;
    private final PetDetailsScreen parent;

    public CheckInStayScreen(
            Pet pet,
            User currentUser,
            StayRepository stayRepository,
            CurrentStaysTablePanel currentStaysTablePanel,
            PetDetailsScreen parent) {

        this.stayRepository = stayRepository;
        this.currentStaysTablePanel = currentStaysTablePanel;
        this.parent = parent;

        setTitle("Check-In Pet: " + pet.getName());
        setSize(400, 250);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField checkOutField = new JTextField();
        JTextField dailyRateField = new JTextField();
        JCheckBox groomingBox = new JCheckBox("Grooming");

        panel.add(new JLabel("Check-Out Date (YYYY-MM-DD):"));
        panel.add(checkOutField);

        panel.add(new JLabel("Daily Rate:"));
        panel.add(dailyRateField);

        panel.add(new JLabel("Grooming:"));
        panel.add(groomingBox);

        JButton saveButton = new JButton("Save");

        saveButton.addActionListener(e ->
                saveNewStay(pet, checkOutField, dailyRateField, groomingBox)
        );


        add(panel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }

    private void saveNewStay(
            Pet pet,
            JTextField checkOutField,
            JTextField dailyRateField,
            JCheckBox groomingBox
    ) {
        int petId = pet.getPetId();
        String checkInDate = LocalDate.now().toString();
        String checkOutDate = checkOutField.getText().trim();
        double dailyRate = Double.parseDouble(dailyRateField.getText().trim());
        int grooming;

        if (groomingBox.isSelected()) {
            grooming = 1;
        } else {
            grooming = 0;
        }

        try {
            Stay stay = new Stay(
                    -1,                         //temporary stayId, will be replaced by DAO
                    petId,
                    checkInDate,
                    checkOutDate,
                    dailyRate,
                    grooming,
                    0.0,                     // totalCost starts at 0 for In Progress
                    "In Progress"
            );

            // Insert into DB
            Stay newStay = StayDAO.addStay(stay);

            if (newStay != null) {
                JOptionPane.showMessageDialog(this, pet.getName() + " checked in successfully.");
                stayRepository.addStay(newStay);
                parent.setCurrentlyBoarded();
                parent.refreshDetails();
                currentStaysTablePanel.loadStaysIntoTable(stayRepository);

                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to check-in " + pet.getName() + ".");
            }


        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

}
