//Package
package com.petboarding.View.DetailViews;

//Imports
import com.petboarding.Models.Owner;
import com.petboarding.View.AppContext;

import javax.swing.*;

public class OwnerDetailsScreen extends JFrame {

    private final AppContext context;

    public OwnerDetailsScreen(Owner owner, AppContext context) {
        this.context = context;
        setTitle("Owner Details");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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