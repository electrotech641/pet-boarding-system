//Package
package com.petboarding.View.DataViews;

//Imports
import com.petboarding.Models.Pet;
import com.petboarding.Models.Stay;
import com.petboarding.Models.User;
import com.petboarding.View.AppContext;
import com.petboarding.View.DetailViews.StayDetailsScreen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Comparator;

public class CurrentStaysTablePanel extends JPanel {

    private final AppContext context;
    private StayDetailsScreen stayDetailsScreen;

    private JTable staysTable;
    private DefaultTableModel tableModel;
    private int lastSortedModelColumn = -1;
    private boolean ascending = true;

    private static final String[] COLUMNS = {
            "Stay ID", "Pet (ID)", "Check-In", "Check-Out",
            "Daily Rate", "Grooming", "Total Cost", "Status"
    };

    public CurrentStaysTablePanel(AppContext context) {

        this.context = context;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 220));

        buildTableModel();
        buildTable();
        configureColumnWidths();
        addListeners();

        add(new JScrollPane(staysTable), BorderLayout.CENTER);

    }

    /*
      -----------------UI Setup--------------------------
     */

    private void buildTableModel() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
    }

    private void buildTable() {
        staysTable = new JTable(tableModel);
        staysTable.setFillsViewportHeight(true);
        staysTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    private void configureColumnWidths() {
        staysTable.getColumnModel().getColumn(0).setPreferredWidth(70);   // Stay ID
        staysTable.getColumnModel().getColumn(1).setPreferredWidth(120);   // PetName(ID)
        staysTable.getColumnModel().getColumn(2).setPreferredWidth(120);  // Check-In
        staysTable.getColumnModel().getColumn(3).setPreferredWidth(120);  // Check-Out
        staysTable.getColumnModel().getColumn(4).setPreferredWidth(120);  // Daily Rate
        staysTable.getColumnModel().getColumn(5).setPreferredWidth(90);   // Grooming
        staysTable.getColumnModel().getColumn(6).setPreferredWidth(120);  // Total Cost
        staysTable.getColumnModel().getColumn(7).setPreferredWidth(120);  // Status
    }

    private void addListeners() {

        //Sorting listener
        staysTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                int viewColumn = staysTable.columnAtPoint(e.getPoint());
                if (viewColumn < 0) return;

                int modelColumn = staysTable.convertColumnIndexToModel(viewColumn);

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
                String colName = staysTable.getColumnName(viewColumn);

                context.statusLabel.setText(
                        "Sorted stays by " + colName + " (" + direction + ") in " + String.format("%.3f ms", ms)
                );
            }
        });

        //Double-click listener
        staysTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                if (e.getClickCount() == 2) {
                    openSelectedStay(context.currentUser);
                }
            }
        });
    }

    /*
        -----------------Data loading + Sorting methods-------------------------
     */

    public void loadStaysIntoTable() {
        tableModel.setRowCount(0);

        for (Stay stay : context.stayRepository.getStayList()) {

            String petName = "Unknown";
            Pet pet = context.petRepository.getPetById(stay.getPetId());
            if (pet != null) {
                petName = pet.getName();
            }

            String petLabel = petName + " (" + stay.getPetId() + ")";
            tableModel.addRow(new Object[]{
                    stay.getStayId(),
                    petLabel,
                    stay.getCheckInDate(),
                    stay.getCheckOutDate(),
                    stay.getDailyRate(),
                    stay.getGrooming(),
                    stay.getTotalCost(),
                    stay.getStatus()
            });
        }
    }

    private void sortByColumn(int columnIndex) {
        Comparator<Stay> comparator = getComparator(columnIndex);

        if (comparator == null) return;
        if (!ascending) comparator = comparator.reversed();

        context.stayRepository.sortStaysBy(comparator);
        context.refreshCurrentStays();
    }

    private Comparator<Stay> getComparator(int columnIndex) {
        switch (columnIndex) {
            case 0: return Comparator.comparingInt(Stay::getStayId);
            //Sort this column by pet name instead of ID
            case 1:
                return Comparator.comparing(
                        stay -> {
                            var pet = context.petRepository.getPetById(stay.getPetId());
                            return pet != null ? pet.getName() : "";
                        },
                        String.CASE_INSENSITIVE_ORDER
                );
            case 2: return Comparator.comparing(Stay::getCheckInDate);
            //When ascending, push null check_out stays to the end
            case 3: return Comparator.comparing(Stay::getCheckOutDate, Comparator.nullsLast(String::compareTo));
            case 4: return Comparator.comparingDouble(Stay::getDailyRate);
            case 5: return Comparator.comparingInt(Stay::getGrooming);
            case 6: return Comparator.comparingDouble(Stay::getTotalCost);
            case 7: return Comparator.comparing(Stay::getStatus);

            default:
                return null;
        }
    }

    private void updateColumnHeader() {

        for (int viewIndex = 0; viewIndex < staysTable.getColumnCount(); viewIndex++) {

            TableColumn column = staysTable.getColumnModel().getColumn(viewIndex);
            int modelIndex = staysTable.convertColumnIndexToModel(viewIndex);

            String baseHeader = COLUMNS[modelIndex];

            if (modelIndex == lastSortedModelColumn) {
                baseHeader += ascending ? " ▲" : " ▼";
            }

            column.setHeaderValue(baseHeader);
        }

        staysTable.getTableHeader().repaint();
    }

    private void openSelectedStay(User currentUser) {
        int viewRow = staysTable.getSelectedRow();
        if (viewRow < 0) return;

        int modelRow = staysTable.convertRowIndexToModel(viewRow);
        int stayId = (int) staysTable.getModel().getValueAt(modelRow, 0);

        Stay stay = context.stayRepository.getStayById(stayId);
        if (stay == null) return;

        // If no window exists, create it
        if (stayDetailsScreen == null) {
            stayDetailsScreen = new StayDetailsScreen(stay, context);

            stayDetailsScreen.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    stayDetailsScreen = null;
                }
            });

            stayDetailsScreen.setVisible(true);
            stayDetailsScreen.setExtendedState(JFrame.NORMAL);
            stayDetailsScreen.toFront();
            stayDetailsScreen.requestFocus();
            return;
        }

        //If window exists, reuse it
        if ((stayDetailsScreen.getExtendedState() & JFrame.ICONIFIED) != 0) {
            stayDetailsScreen.setExtendedState(JFrame.NORMAL);
        }

        try {
            stayDetailsScreen.loadStay(stay);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        stayDetailsScreen.toFront();
        stayDetailsScreen.requestFocus();
    }


}
