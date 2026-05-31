//Packages
package com.petboarding.View.DataViews;

//Imports
import com.petboarding.Repository.OwnerRepository;
import com.petboarding.Repository.PetRepository;
import com.petboarding.Models.Pet;
import com.petboarding.Models.User;
import com.petboarding.View.DetailViews.PetDetailsScreen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;

public class PetTablePanel extends JPanel {

    private JTable petTable;
    private DefaultTableModel tableModel;
    private PetRepository petRepository;
    private JLabel statusLabel;

    //Track whether sorted ascending or descending
    private int lastSortedColumn = -1;
    private boolean ascending = true;

    public PetTablePanel(PetRepository petRepository, OwnerRepository ownerRepository, User currentUser, JLabel statusLabel) {

        this.petRepository = petRepository;
        this.statusLabel = statusLabel;

        setLayout(new BorderLayout());

        //Column header labels
        String[] columns = {"ID", "Name", "Species", "Age", "Owner ID", "Notes"};

        //Read only table
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        petTable = new JTable(tableModel);
        petTable.setFillsViewportHeight(true);
        petTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        petTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        petTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        petTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        petTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        petTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        petTable.getColumnModel().getColumn(5).setPreferredWidth(550);

        loadPetsIntoTable(petRepository);

        add(new JScrollPane(petTable), BorderLayout.CENTER);

        //Mouse listener for by-column sorting and pet double click for details
        petTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int column = petTable.columnAtPoint(e.getPoint());

                //If click the same column header, flip sorting order
                if (column == lastSortedColumn) {
                    ascending = !ascending;
                }
                else {
                    ascending = true;
                }
                lastSortedColumn = column;

                //Measure sorting efficiency
                long start = System.nanoTime();

                sortByColumn(column);
                updateColumnHeader();

                long end = System.nanoTime();
                double ms = (end - start) / 1_000_000.0;

                //Update the main screen status label to display sorting efficiency
                String direction;
                if (ascending) {
                    direction = "ascending";
                } else {
                    direction = "descending";
                }

                String colName = petTable.getColumnName(column);

                statusLabel.setText(
                        "Sorted by " + colName + " (" + direction + ") in " + String.format("%.3f ms", ms)
                );
            }
        });

        petTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && petTable.getSelectedRow() != -1) {

                    int row = petTable.getSelectedRow();
                    int petId = (int) petTable.getValueAt(row, 0);

                    Pet pet = petRepository.getPetById(petId);

                    new PetDetailsScreen(pet, currentUser, ownerRepository).setVisible(true);

                }
            }
        });
    }

    public void loadPetsIntoTable(PetRepository petRepository) {
        tableModel.setRowCount(0);

        for (Pet pet : petRepository.getPetList()) {
            tableModel.addRow(new Object[]{
                    pet.getPetId(),
                    pet.getName(),
                    pet.getSpecies(),
                    pet.getAge(),
                    pet.getOwnerId(),
                    pet.getNotes()
            });
        }
    }

    //Determines what order to feed into my merge sort algorithm, ascending or descending
    private void sortByColumn(int columnIndex) {
        Comparator<Pet> comparator = null;

        switch (columnIndex) {
            case 0: // ID
                if (ascending) {
                    comparator = Comparator.comparingInt(Pet::getPetId);
                } else {
                    comparator = (p1, p2) -> Integer.compare(p2.getPetId(), p1.getPetId());
                }
                break;

            case 1: // Name
                if (ascending) {
                    comparator = (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName());
                } else {
                    comparator = (p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName());
                }
                break;

            case 2: // Species
                if (ascending) {
                    comparator = (p1, p2) -> p1.getSpecies().compareToIgnoreCase(p2.getSpecies());
                } else {
                    comparator = (p1, p2) -> p2.getSpecies().compareToIgnoreCase(p1.getSpecies());
                }
                break;

            case 3: // Age
                if (ascending) {
                    comparator = Comparator.comparingInt(Pet::getAge);
                } else {
                    comparator = (p1, p2) -> Integer.compare(p2.getAge(), p1.getAge());
                }
                break;

            case 4: // Owner ID
                if (ascending) {
                    comparator = Comparator.comparingInt(Pet::getOwnerId);
                } else {
                    comparator = (p1, p2) -> Integer.compare(p2.getOwnerId(), p1.getOwnerId());
                }
                break;

            case 5: // Notes
                if (ascending) {
                    comparator = (p1, p2) -> p1.getNotes().compareToIgnoreCase(p2.getNotes());
                } else {
                    comparator = (p1, p2) -> p2.getNotes().compareToIgnoreCase(p1.getNotes());
                }
                break;
        }

        //Sorts pets by chosen column, asc or desc, and loads pets into table
        petRepository.sortPetsBy(comparator);
        loadPetsIntoTable(petRepository);
    }

    private void updateColumnHeader() {
        String[] columns = {"ID", "Name", "Species", "Age", "Owner ID", "Notes"};

        //Append an up arrow or down arrow depending on sort order and last clicked column
        for (int i = 0; i < columns.length; i++) {
            String arrow;
            if (i == lastSortedColumn) {

                if (ascending) {
                    arrow = " ▲";
                }
                else {
                    arrow = " ▼";
                }
                petTable.getColumnModel().getColumn(i).setHeaderValue(columns[i] + arrow);
            }
            else {
                petTable.getColumnModel().getColumn(i).setHeaderValue(columns[i]);
            }

        }

        //redraw the headers
        petTable.getTableHeader().repaint();
    }
}
