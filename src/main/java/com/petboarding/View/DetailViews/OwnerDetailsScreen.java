package com.petboarding.View.DetailViews;

import com.petboarding.Models.Owner;
import com.petboarding.Models.User;

import javax.swing.*;

public class OwnerDetailsScreen extends JFrame {

    public OwnerDetailsScreen(Owner owner, User user) {
        setTitle("Owner Details");
        setSize(350, 250);
        setLocationRelativeTo(null);


        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if (owner == null) {
            panel.add(new JLabel("Owner not found."));
        } else {
            panel.add(new JLabel("ID: " + owner.getOwnerId()));
            panel.add(new JLabel("Name: " + owner.getName()));
            panel.add(new JLabel("Phone: " + owner.getPhone()));
            panel.add(new JLabel("Email: " + owner.getEmail()));
            panel.add(new JLabel("Address: " + owner.getAddress()));
        }

        add(panel);
    }
}