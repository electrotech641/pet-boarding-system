//Package
package com.petboarding.View.DetailViews;

//Imports
import com.petboarding.Models.Owner;
import com.petboarding.View.AppContext;

import javax.swing.*;

public class OwnerDetailsScreen extends JFrame {

    private final AppContext context;
    private Owner owner;

    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel phoneLabel;
    private JLabel emailLabel;
    private JLabel addressLabel;

    public OwnerDetailsScreen(Owner owner, AppContext context) {
        this.context = context;
        this.owner = owner;

        setTitle("Owner Details");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        idLabel = new JLabel();
        nameLabel = new JLabel();
        phoneLabel = new JLabel();
        emailLabel = new JLabel();
        addressLabel = new JLabel();

        panel.add(idLabel);
        panel.add(nameLabel);
        panel.add(phoneLabel);
        panel.add(emailLabel);
        panel.add(addressLabel);

        add(panel);

        refreshDetails();
    }

    public void refreshDetails() {
        if (owner == null) {
            idLabel.setText("ID: N/A");
            nameLabel.setText("Name: N/A");
            phoneLabel.setText("Phone: N/A");
            emailLabel.setText("Email: N/A");
            addressLabel.setText("Address: N/A");
            return;
        }

        idLabel.setText("ID: " + owner.getOwnerId());
        nameLabel.setText("Name: " + owner.getName());
        phoneLabel.setText("Phone: " + owner.getPhone());
        emailLabel.setText("Email: " + owner.getEmail());
        addressLabel.setText("Address: " + owner.getAddress());
    }

    public void loadOwner(Owner owner) {
        this.owner = owner;
        refreshDetails();
    }
}
