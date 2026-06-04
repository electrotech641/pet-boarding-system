//Package
package com.petboarding.View.SearchResultsViews;

//Imports
import com.petboarding.Models.Owner;
import com.petboarding.View.AppContext;
import com.petboarding.View.DetailViews.OwnerDetailsScreen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class OwnerSearchResultsScreen extends JFrame {

    private JTable table;
    private List<Owner> owners;
    private final AppContext context;

    public OwnerSearchResultsScreen(List<Owner> owners, AppContext context) {
        this.owners = owners;
        this.context = context;

        setTitle("Owner Search Results");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        //Table model
        String[] cols = {"Owner ID", "Name", "Phone", "Email", "Address"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        //Construct the table view of results
        for (Owner owner : owners) {
            model.addRow(new Object[]{
                    owner.getOwnerId(),
                    owner.getName(),
                    owner.getPhone(),
                    owner.getEmail(),
                    owner.getAddress()
            });
        }

        table = new JTable(model);

        //Double click listener
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedOwner();
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        add(closeButton, BorderLayout.SOUTH);
    }

    private void openSelectedOwner() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;

        //Convert view row to model row
        //Initially did not do this, had a bug when user dragged ID column before selecting
        int modelRow = table.convertRowIndexToModel(viewRow);

        //OwnerId is always column 0 in the model row
        int ownerId = Integer.parseInt(table.getModel().getValueAt(modelRow, 0).toString());

        Owner selected = null;

        for (Owner owner : owners) {
            if (owner.getOwnerId() == ownerId) {
                selected = owner;
                break;
            }
        }

        if (selected != null) {
            new OwnerDetailsScreen(selected, context).setVisible(true);
        }
    }

}
