//Package
package com.petboarding.View.DataViews;

//Imports
import com.petboarding.Models.*;
import com.petboarding.View.AppContext;
import com.petboarding.View.DetailViews.PetDetailsScreen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Comparator;

public class PetTablePanel extends JPanel {

    private JTable petTable;
    private DefaultTableModel tableModel;
    private int lastSortedModelColumn = -1;
    private boolean ascending = true;
    private PetDetailsScreen petDetailsScreen;

    private final AppContext context;
    private static final String[] COLUMNS =
            {"ID", "Name", "Species", "Age", "Owner (ID)", "Notes"};

    public PetTablePanel(AppContext context) {

        this.context = context;

        setLayout(new BorderLayout());

        buildTableModel();
        buildTable();
        configureColumnWidths();
        addListeners();

        add(new JScrollPane(petTable), BorderLayout.CENTER);
    }

    /*
        ---------------UI Builder Methods------------------------
     */

    private void buildTableModel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void buildTable() {
        petTable = new JTable(tableModel);
        petTable.setFillsViewportHeight(true);
        petTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    private void configureColumnWidths() {
        petTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        petTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Name
        petTable.getColumnModel().getColumn(2).setPreferredWidth(80);   // Species
        petTable.getColumnModel().getColumn(3).setPreferredWidth(50);   // Age
        petTable.getColumnModel().getColumn(4).setPreferredWidth(200);   // Owner Name(ID)
        petTable.getColumnModel().getColumn(5).setPreferredWidth(550);  // Notes
    }

    private void addListeners() {

        // Sorting listener
        petTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewColumn = petTable.columnAtPoint(e.getPoint());
                if (viewColumn < 0) return;

                int modelColumn = petTable.convertColumnIndexToModel(viewColumn);

                if (modelColumn == lastSortedModelColumn) {
                    ascending = !ascending;
                } else {
                    ascending = true;
                }

                lastSortedModelColumn = modelColumn;

                long start = System.nanoTime();
                sortByColumn(modelColumn);
                updateColumnHeader();
                long end = System.nanoTime();

                double ms = (end - start) / 1000000.0;
                String direction = ascending ? "ascending" : "descending";
                String colName = petTable.getColumnName(viewColumn);

                context.statusLabel.setText(
                        "Sorted by " + colName + " (" + direction + ") in " + String.format("%.3f ms", ms)
                );
            }
        });

        // Double-click listener
        petTable.addMouseListener(new MouseAdapter() {
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
    }

    /*
        ----------------Data loading + Sorting Methods-------------------------
     */

    public void loadPetsIntoTable() {
        tableModel.setRowCount(0);

        for (Pet pet : context.petRepository.getPetList()) {

            Owner owner = context.ownerRepository.getOwnerById(pet.getOwnerId());
            String ownerLabel;
            if (owner != null) {
                ownerLabel = owner.getName() + " (" + owner.getOwnerId() + ")";
            } else {
                ownerLabel = "Unknown";
            }

            tableModel.addRow(new Object[]{
                    pet.getPetId(),
                    pet.getName(),
                    pet.getSpecies(),
                    pet.getAge(),
                    ownerLabel,
                    pet.getNotes()
            });
        }
    }

    //Determines what order to feed into my merge sort algorithm, ascending or descending
    private void sortByColumn(int columnIndex) {
        Comparator<Pet> comparator = getComparator(columnIndex);

        if (comparator == null) return;
        if (!ascending) comparator = comparator.reversed();

        context.petRepository.sortPetsBy(comparator);
        loadPetsIntoTable();
    }

    private Comparator<Pet> getComparator(int columnIndex) {
        switch (columnIndex) {
            case 0: return Comparator.comparingInt(Pet::getPetId);
            case 1: return Comparator.comparing(Pet::getName, String.CASE_INSENSITIVE_ORDER);
            case 2: return Comparator.comparing(Pet::getSpecies, String.CASE_INSENSITIVE_ORDER);
            case 3: return Comparator.comparingInt(Pet::getAge);
            //Compare this column by owner name instead of Owner ID
            case 4: return Comparator.comparing(
                    pet -> {
                        Owner owner = context.ownerRepository.getOwnerById(pet.getOwnerId());
                        return owner != null ? owner.getName() : "";
                    },
                    String.CASE_INSENSITIVE_ORDER
            );
            case 5: return Comparator.comparing(Pet::getNotes, String.CASE_INSENSITIVE_ORDER);
            default: return null;
        }
    }

    private void updateColumnHeader() {
        // Loop over *view* columns
        for (int viewIndex = 0; viewIndex < petTable.getColumnCount(); viewIndex++) {
            TableColumn column = petTable.getColumnModel().getColumn(viewIndex);

            // Get the model index for this view column
            int modelIndex = petTable.convertColumnIndexToModel(viewIndex);

            // Base header text from COLUMNS[modelIndex]
            String baseHeader = COLUMNS[modelIndex];

            if (modelIndex == lastSortedModelColumn) {
                baseHeader += ascending ? " ▲" : " ▼";
            }

            column.setHeaderValue(baseHeader);
        }

        petTable.getTableHeader().repaint();
    }

    private void openSelectedPet() throws SQLException {
        int viewRow = petTable.getSelectedRow();
        if (viewRow < 0) return;

        int modelRow = petTable.convertRowIndexToModel(viewRow);
        int petId = (int) petTable.getModel().getValueAt(modelRow, 0);

        Pet pet = context.petRepository.getPetById(petId);
        if (pet == null) return;

        // If no window exists, create it
        if (petDetailsScreen == null) {
            petDetailsScreen = new PetDetailsScreen(pet, context);

            petDetailsScreen.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    petDetailsScreen = null;
                }
            });

            petDetailsScreen.setVisible(true);
            petDetailsScreen.setExtendedState(JFrame.NORMAL);
            petDetailsScreen.toFront();
            petDetailsScreen.requestFocus();
            return;
        }

        //Load new pet in existing pet details screen
        petDetailsScreen.loadPet(pet);

        //Bring window into focus
        if ((petDetailsScreen.getExtendedState() & JFrame.ICONIFIED) != 0) {
            petDetailsScreen.setExtendedState(JFrame.NORMAL);
        }

        petDetailsScreen.toFront();
        petDetailsScreen.requestFocus();
    }

}
