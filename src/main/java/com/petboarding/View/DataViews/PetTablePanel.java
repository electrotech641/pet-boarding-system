//Package
package com.petboarding.View.DataViews;

//Imports
import com.petboarding.Repository.OwnerRepository;
import com.petboarding.Repository.PetRepository;
import com.petboarding.Models.*;
import com.petboarding.Repository.StayRepository;
import com.petboarding.View.DetailViews.PetDetailsScreen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Comparator;

public class PetTablePanel extends JPanel {

    private JTable petTable;
    private DefaultTableModel tableModel;

    //Persistent objects
    private final JLabel statusLabel;
    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;
    private final StayRepository stayRepository;
    private final CurrentStaysTablePanel currentStaysTablePanel;

    private int lastSortedModelColumn = -1;
    private boolean ascending = true;

    private static final String[] COLUMNS =
            {"ID", "Name", "Species", "Age", "Owner (ID)", "Notes"};

    public PetTablePanel(PetRepository petRepository,
                         OwnerRepository ownerRepository,
                         StayRepository stayRepository,
                         CurrentStaysTablePanel currentStaysTablePanel,
                         User currentUser,
                         JLabel statusLabel) {

        this.petRepository = petRepository;
        this.ownerRepository = ownerRepository;
        this.stayRepository = stayRepository;
        this.currentStaysTablePanel = currentStaysTablePanel;
        this.statusLabel = statusLabel;

        setLayout(new BorderLayout());

        buildTableModel();
        buildTable();
        configureColumnWidths();
        loadPetsIntoTable(petRepository);
        addListeners(currentUser);

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

    private void addListeners(User currentUser) {

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

                statusLabel.setText(
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
                        openSelectedPet(currentUser);
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

    public void loadPetsIntoTable(PetRepository petRepository) {
        tableModel.setRowCount(0);

        for (Pet pet : petRepository.getPetList()) {

            Owner owner = ownerRepository.getOwnerById(pet.getOwnerId());
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

        petRepository.sortPetsBy(comparator);
        loadPetsIntoTable(petRepository);
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
                        Owner owner = ownerRepository.getOwnerById(pet.getOwnerId());
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

    private void openSelectedPet(User currentUser) throws SQLException {
        int viewRow = petTable.getSelectedRow();
        if (viewRow < 0) return;

        //Convert view row to model row
        //Initially did not do this, had a bug when user dragged ID column before selecting
        int modelRow = petTable.convertRowIndexToModel(viewRow);

        //PetId is always column 0 in the model row
        int petId = (int) petTable.getModel().getValueAt(modelRow, 0);

        Pet pet = petRepository.getPetById(petId);

        if (pet != null) {
            new PetDetailsScreen(pet, currentUser, ownerRepository, stayRepository, currentStaysTablePanel, petRepository).setVisible(true);
        }
    }
}
