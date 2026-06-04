//Package
package com.petboarding.View.DetailViews;

//Imports
import com.petboarding.Models.Owner;
import com.petboarding.Repository.OwnerRepository;
import com.petboarding.View.CreateViews.CreatePetScreen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class OwnerSearchDialog extends JDialog {

    private JTable table;
    private final OwnerRepository ownerRepository;
    private final CreatePetScreen parent;

    public OwnerSearchDialog(OwnerRepository ownerRepository, CreatePetScreen parent) {
        super(parent, "Select Owner", true);
        this.ownerRepository = ownerRepository;
        this.parent = parent;

        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        String[] cols = {"Owner ID", "Name", "Phone"};

        //Define the table model to be uneditable
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing
            }
        };

        //Fill the model with pertinent owner data
        for (Owner owner : ownerRepository.getOwnerList()) {
            model.addRow(new Object[]{
                    owner.getOwnerId(),
                    owner.getName(),
                    owner.getPhone()
            });
        }

        //Construct the layout
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton selectButton = new JButton("Select Owner");
        add(selectButton, BorderLayout.SOUTH);

        //Double click listener for selecting and selectButton listener
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectOwnerFromTable();
                }
            }
        });

        selectButton.addActionListener(e -> selectOwnerFromTable());
    }

    private void selectOwnerFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an owner.");
            return;
        }

        int ownerId = Integer.parseInt(table.getValueAt(row, 0).toString());
        Owner owner = ownerRepository.getOwnerById(ownerId);

        parent.setSelectedOwner(owner);
        dispose();
    }
}
