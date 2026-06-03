//Package
package com.petboarding.View.DataViews;

//Imports
import com.petboarding.Repository.OwnerRepository;
import com.petboarding.Models.Owner;
import com.petboarding.Models.User;
import com.petboarding.View.DetailViews.OwnerDetailsScreen;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;

public class OwnerTablePanel extends JPanel {
    private JTable ownerTable;
    private DefaultTableModel tableModel;
    private OwnerRepository ownerRepository;
    private JLabel statusLabel;

    private int lastSortedModelColumn = -1;
    private boolean ascending = true;

    private static final String[] COLUMNS = {"ID", "Name", "Phone", "Email", "Address"};

    public OwnerTablePanel(OwnerRepository ownerRepository, User currentUser, JLabel statusLabel) {
        this.ownerRepository = ownerRepository;
        this.statusLabel = statusLabel;

        setLayout(new BorderLayout());

        if (currentUser.isReadOnly()) {
            showReadOnlyMessage();
            return;
        }

        buildTableModel();
        buildTable();
        configureColumnWidths();
        loadOwnersIntoTable(ownerRepository);
        addListeners(currentUser);

        add(new JScrollPane(ownerTable), BorderLayout.CENTER);
    }

    /*
        -------------------UI Builder Methods-----------------------------
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
        ownerTable = new JTable(tableModel);
        ownerTable.setFillsViewportHeight(true);
        ownerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    private void configureColumnWidths() {
        ownerTable.getColumnModel().getColumn(0).setPreferredWidth(50);     // ID
        ownerTable.getColumnModel().getColumn(1).setPreferredWidth(200);    // Name
        ownerTable.getColumnModel().getColumn(2).setPreferredWidth(150);    // Phone
        ownerTable.getColumnModel().getColumn(3).setPreferredWidth(250);    // Email
        ownerTable.getColumnModel().getColumn(4).setPreferredWidth(450);    // Address
    }

    private void addListeners(User currentUser) {

        // Sorting listener (view → model safe)
        ownerTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int viewColumn = ownerTable.columnAtPoint(e.getPoint());
                if (viewColumn < 0) return;

                int modelColumn = ownerTable.convertColumnIndexToModel(viewColumn);

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

                double ms = (end - start) / 1_000_000.0;
                String direction = ascending ? "ascending" : "descending";
                String colName = ownerTable.getColumnName(viewColumn);

                statusLabel.setText(
                        "Sorted owners by " + colName + " (" + direction + ") in " + String.format("%.3f ms", ms)
                );
            }
        });

        // Double-click listener
        ownerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedOwner(currentUser);
                }
            }
        });
    }

    /*
        ------------------Data loading + Sorting Methods-------------------------------------
     */
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
        Comparator<Owner> comparator = getComparator(columnIndex);
        if (!ascending) comparator = comparator.reversed();

        ownerRepository.sortOwnersBy(comparator);
        loadOwnersIntoTable(ownerRepository);
    }

    private Comparator<Owner> getComparator(int columnIndex) {
        switch (columnIndex) {
            case 0: return Comparator.comparingInt(Owner::getOwnerId);
            case 1: return Comparator.comparing(Owner::getName, String.CASE_INSENSITIVE_ORDER);
            case 2: return Comparator.comparing(Owner::getPhone, String.CASE_INSENSITIVE_ORDER);
            case 3: return Comparator.comparing(Owner::getEmail, String.CASE_INSENSITIVE_ORDER);
            case 4: return Comparator.comparing(Owner::getAddress, String.CASE_INSENSITIVE_ORDER);
            default: return null;
        }
    }

    private void updateColumnHeader() {

        for (int viewIndex = 0; viewIndex < ownerTable.getColumnCount(); viewIndex++) {

            TableColumn column = ownerTable.getColumnModel().getColumn(viewIndex);
            int modelIndex = ownerTable.convertColumnIndexToModel(viewIndex);

            String baseHeader = COLUMNS[modelIndex];

            if (modelIndex == lastSortedModelColumn) {
                baseHeader += ascending ? " ▲" : " ▼";
            }

            column.setHeaderValue(baseHeader);
        }

        ownerTable.getTableHeader().repaint();
    }

    /*
        --------Method to open selected owner details screen--------------------
     */
    private void openSelectedOwner(User currentUser) {
        int viewRow = ownerTable.getSelectedRow();
        if (viewRow < 0) return;

        //Convert view row to model row
        //Initially did not do this, had a bug when user dragged ID column before selecting
        int modelRow = ownerTable.convertRowIndexToModel(viewRow);

        //OwnerId is always column 0 in the model row
        int ownerId = (int) ownerTable.getModel().getValueAt(modelRow, 0);

        Owner owner = ownerRepository.getOwnerById(ownerId);
        if (owner != null && currentUser.isAdmin()) {
            new OwnerDetailsScreen(owner, currentUser).setVisible(true);
        }
    }

    /*
        -------------------------------READ_ONLY user message------------------------
     */
    private void showReadOnlyMessage() {
        JLabel message = new JLabel("Owner data only accessible to staff and admin", SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.BOLD, 16));
        add(message, BorderLayout.CENTER);
    }
}