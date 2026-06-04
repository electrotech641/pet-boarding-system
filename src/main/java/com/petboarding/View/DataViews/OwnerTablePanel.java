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
import java.util.Comparator;

public class OwnerTablePanel extends JPanel {
    private JTable ownerTable;
    private DefaultTableModel tableModel;
    private final AppContext context;

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

        //Convert view row to model row
        //Initially did not do this, had a bug when user dragged ID column before selecting
        int modelRow = ownerTable.convertRowIndexToModel(viewRow);

        //OwnerId is always column 0 in the model row
        int ownerId = (int) ownerTable.getModel().getValueAt(modelRow, 0);

        Owner owner = context.ownerRepository.getOwnerById(ownerId);
        if (owner != null && context.currentUser.isAdmin()) {
            new OwnerDetailsScreen(owner, context).setVisible(true);
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