//Package
package com.petboarding.View.SearchResultsViews;

//Imports
import com.petboarding.Models.Pet;
import com.petboarding.View.AppContext;
import com.petboarding.View.DetailViews.PetDetailsScreen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class PetSearchResultsScreen extends JFrame {

    private JTable table;
    private List<Pet> pets;
    private final AppContext context;

    public PetSearchResultsScreen(List<Pet> pets,
                                  AppContext context) {
        this.pets = pets;
        this.context = context;

        setTitle("Search Results (" + pets.size() + " found)");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        //Table model
        String[] cols = {"Pet ID", "Name", "Species", "Age", "Owner"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        //Construct the table view of results
        for (Pet pet : pets) {
            String ownerName = context.ownerRepository.getOwnerById(pet.getOwnerId()).getName();

            model.addRow(new Object[]{
                    pet.getPetId(),
                    pet.getName(),
                    pet.getSpecies(),
                    pet.getAge(),
                    ownerName
            });
        }

        table = new JTable(model);

        //Double-click listener
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    try {
                        openSelectedPet();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        add(closeButton, BorderLayout.SOUTH);
    }

    private void openSelectedPet() throws SQLException {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;

        //Convert view row to model row
        //Initially did not do this, had a bug when user dragged ID column before selecting
        int modelRow = table.convertRowIndexToModel(viewRow);

        //PetId is always column 0 in the model row
        int petId = Integer.parseInt(table.getModel().getValueAt(modelRow, 0).toString());

        Pet selected = null;

        for (Pet pet : pets) {
            if (pet.getPetId() == petId) {
                selected = pet;
                break;
            }
        }

        if (selected != null) {
            new PetDetailsScreen(selected, context).setVisible(true);
        }
    }
}
