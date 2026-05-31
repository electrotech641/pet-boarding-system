//Packages
package com.petboarding.View.DataViews;

//Imports
import com.petboarding.Repository.OwnerRepository;
import com.petboarding.Models.Owner;
import com.petboarding.Models.User;
import com.petboarding.View.DetailViews.OwnerDetailsScreen;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;

public class OwnerTablePanel extends JPanel {
    private JTable ownerTable;
    private DefaultTableModel tableModel;
    private OwnerRepository ownerRepository;
    private JLabel statusLabel;

    private int lastSortedColumn = -1;
    private boolean ascending = true;

    public OwnerTablePanel(OwnerRepository ownerRepository, User currentUser, JLabel statusLabel) {
        this.ownerRepository = ownerRepository;
        this.statusLabel = statusLabel;

        setLayout(new BorderLayout());

        String[] columns = {"ID", "Name", "Phone", "Email", "Address"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ownerTable = new JTable(tableModel);
        ownerTable.setFillsViewportHeight(true);
        ownerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        ownerTable.getColumnModel().getColumn(0).setPreferredWidth(50);     //id
        ownerTable.getColumnModel().getColumn(1).setPreferredWidth(200);    //name
        ownerTable.getColumnModel().getColumn(2).setPreferredWidth(150);    //phone
        ownerTable.getColumnModel().getColumn(3).setPreferredWidth(250);    //email
        ownerTable.getColumnModel().getColumn(4).setPreferredWidth(450);    //address


        loadOwnersIntoTable(ownerRepository);

        add(new JScrollPane(ownerTable), BorderLayout.CENTER);

        ownerTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = ownerTable.columnAtPoint(e.getPoint());

                if (column == lastSortedColumn) {
                    ascending = !ascending;
                } else {
                    ascending = true;
                }

                lastSortedColumn = column;

                long start = System.nanoTime();

                sortByColumn(column);
                updateColumnHeader();

                long end = System.nanoTime();
                double ms = (end - start) / 1_000_000.0;

                String direction;
                if (ascending) {
                    direction = "ascending";
                } else {
                    direction = "descending";
                }

                String colName = ownerTable.getColumnName(column);

                statusLabel.setText("Sorted owners by " + colName + " (" + direction + ") in " + String.format("%.3f ms", ms));
            }
        });

        ownerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && ownerTable.getSelectedRow() != -1) {
                    if (currentUser.isAdmin()) {
                        int row = ownerTable.getSelectedRow();
                        int petId = (int) ownerTable.getValueAt(row, 0);

                        Owner owner = ownerRepository.getOwnerById(petId);

                        new OwnerDetailsScreen(owner, currentUser).setVisible(true);
                    }
                }
            }
        });
    }

    public void loadOwnersIntoTable(OwnerRepository ownerRepository) {
        tableModel.setRowCount(0);

        for (Owner owner : ownerRepository.getOwnerList()) {
            tableModel.addRow(new Object[]{
                    owner.getOwnerId(),
                    owner.getName(),
                    owner.getPhone(),
                    owner.getEmail(),
                    owner.getAddress()
            });
        }
    }

    private void sortByColumn(int columnIndex) {
        Comparator<Owner> comparator = null;

        switch (columnIndex) {
            case 0: // ID
                comparator = ascending
                        ? Comparator.comparingInt(Owner::getOwnerId)
                        : (o1, o2) -> Integer.compare(o2.getOwnerId(), o1.getOwnerId());
                break;

            case 1: // Name
                comparator = ascending
                        ? Comparator.comparing(Owner::getName, String.CASE_INSENSITIVE_ORDER)
                        : (o1, o2) -> o2.getName().compareToIgnoreCase(o1.getName());
                break;

            case 2: // Phone
                comparator = ascending
                        ? Comparator.comparing(Owner::getPhone, String.CASE_INSENSITIVE_ORDER)
                        : (o1, o2) -> o2.getPhone().compareToIgnoreCase(o1.getPhone());
                break;

            case 3: // Email
                comparator = ascending
                        ? Comparator.comparing(Owner::getEmail, String.CASE_INSENSITIVE_ORDER)
                        : (o1, o2) -> o2.getEmail().compareToIgnoreCase(o1.getEmail());
                break;

            case 4: // Address
                comparator = ascending
                        ? Comparator.comparing(Owner::getAddress, String.CASE_INSENSITIVE_ORDER)
                        : (o1, o2) -> o2.getAddress().compareToIgnoreCase(o1.getAddress());
                break;
        }

        ownerRepository.sortOwnersBy(comparator);
        loadOwnersIntoTable(ownerRepository);
    }

    private void updateColumnHeader() {
        String[] columns = {"ID", "Name", "Phone", "Email", "Address"};

        for (int i = 0; i < ownerTable.getColumnCount(); i++) {
            String arrow;
            if (i == lastSortedColumn) {

                if (ascending) {
                    arrow = " ▲";
                } else {
                    arrow = " ▼";
                }

                ownerTable.getColumnModel().getColumn(i).setHeaderValue(columns[i] + arrow);
            } else {
                ownerTable.getColumnModel().getColumn(i).setHeaderValue(columns[i]);
            }
        }

        ownerTable.getTableHeader().repaint();
    }
}