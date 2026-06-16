//Package
package com.petboarding.View.DataViews;

//Imports
import com.petboarding.Models.Owner;
import com.petboarding.View.AppContext;
import com.petboarding.View.DetailViews.OwnerDetailsScreen;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Comparator;

public class OwnerTablePanel extends JPanel {

    private final AppContext context;
    private OwnerDetailsScreen ownerDetailsScreen;

    private JTable ownerTable;
    private DefaultTableModel tableModel;
    private int lastSortedModelColumn = -1;
    private boolean ascending = true;

    private static final String[] COLUMNS = {"ID", "Name", "Phone", "Email", "Address"};

    public OwnerTablePanel(AppContext context) {
        this.context = context;

        setLayout(new BorderLayout());

        if (context.currentUser.isReadOnly()) {
            showReadOnlyMessage();
            return;
        }

        buildTableModel();
        buildTable();
        configureColumnWidths();
        addListeners();

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

    private void addListeners() {

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

                context.statusLabel.setText(
                        "Sorted owners by " + colName + " (" + direction + ") in " + String.format("%.3f ms", ms)
                );
            }
        });

        // Double-click listener
        ownerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedOwner();
                }
            }
        });
    }

    /*
        ------------------Data loading + Sorting Methods-------------------------------------
     */
    public void loadOwnersIntoTable() {
        if (context.currentUser.isReadOnly()) {
            return;
        }

        tableModel.setRowCount(0);

        for (Owner owner : context.ownerRepository.getOwnerList()) {
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

        context.ownerRepository.sortOwnersBy(comparator);
        loadOwnersIntoTable();
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
    private void openSelectedOwner() {
        int viewRow = ownerTable.getSelectedRow();
        if (viewRow < 0) return;

        int modelRow = ownerTable.convertRowIndexToModel(viewRow);
        int ownerId = (int) ownerTable.getModel().getValueAt(modelRow, 0);

        Owner owner = context.ownerRepository.getOwnerById(ownerId);
        if (owner == null) return;

        // If the window does not exist, create it
        if (ownerDetailsScreen == null) {
            ownerDetailsScreen = new OwnerDetailsScreen(owner, context);

            ownerDetailsScreen.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    ownerDetailsScreen = null;
                }
            });

            ownerDetailsScreen.setVisible(true);
            ownerDetailsScreen.setExtendedState(JFrame.NORMAL);
            ownerDetailsScreen.toFront();
            ownerDetailsScreen.requestFocus();
            return;
        }

        // If it DOES exist, reuse it
        // Restore if minimized
        if ((ownerDetailsScreen.getExtendedState() & JFrame.ICONIFIED) != 0) {
            ownerDetailsScreen.setExtendedState(JFrame.NORMAL);
        }

        // Load the new owner into the existing window
        ownerDetailsScreen.loadOwner(owner);

        // Bring it forward
        ownerDetailsScreen.toFront();
        ownerDetailsScreen.requestFocus();
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