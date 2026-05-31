package com.petboarding.View.DataViews;

import com.petboarding.Repository.OwnerRepository;
import com.petboarding.Repository.PetRepository;
import com.petboarding.Repository.StayRepository;;
import com.petboarding.Models.Stay;
import com.petboarding.Models.User;
import com.petboarding.View.DetailViews.StayDetailsScreen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.Comparator;

public class CurrentStaysTablePanel extends JPanel {

    private JTable staysTable;
    private DefaultTableModel tableModel;
    private StayRepository stayRepository;
    private PetRepository petRepository;
    private OwnerRepository ownerRepository;
    private User currentUser;
    private JLabel statusLabel;

    private int lastSortedColumn = -1;
    private boolean ascending = true;

    public CurrentStaysTablePanel(StayRepository stayRepository,
                                  PetRepository petRepository,
                                  OwnerRepository ownerRepository,
                                  User user, JLabel statusLabel) {

        this.stayRepository = stayRepository;
        this.petRepository = petRepository;
        this.ownerRepository = ownerRepository;
        this.currentUser = user;
        this.statusLabel = statusLabel;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 220)); // Good height for top panel

        String[] columns = {
                "Stay ID", "Pet ID", "Check-In", "Check-Out", "Daily Rate",
                "Grooming", "Total Cost", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        staysTable = new JTable(tableModel);
        staysTable.setFillsViewportHeight(true);
        staysTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Column widths
        staysTable.getColumnModel().getColumn(0).setPreferredWidth(70);   //stay_id
        staysTable.getColumnModel().getColumn(1).setPreferredWidth(70);   //pet_id
        staysTable.getColumnModel().getColumn(2).setPreferredWidth(150);  //check_in
        staysTable.getColumnModel().getColumn(3).setPreferredWidth(150);  //check_out
        staysTable.getColumnModel().getColumn(4).setPreferredWidth(150);  //daily_rate
        staysTable.getColumnModel().getColumn(5).setPreferredWidth(90);   //grooming
        staysTable.getColumnModel().getColumn(6).setPreferredWidth(120);  //total_cost
        staysTable.getColumnModel().getColumn(7).setPreferredWidth(120);  //status

        loadStaysIntoTable(stayRepository);

        add(new JScrollPane(staysTable), BorderLayout.CENTER);

        //Sorting Listener
        staysTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int column = staysTable.columnAtPoint(e.getPoint());

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

                String direction = ascending ? "ascending" : "descending";
                String colName = staysTable.getColumnName(column);

                statusLabel.setText(
                        "Sorted stays by " + colName + " (" + direction + ") in " + String.format("%.3f ms", ms)
                );
            }
        });

        //Double click to open StayDetailsScreen
        staysTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {

                    int row = staysTable.getSelectedRow();
                    if (row < 0) return;

                    // Assuming column 0 is Stay ID
                    int stayId = (int) staysTable.getValueAt(row, 0);

                    Stay stay = stayRepository.getStayById(stayId);
                    if (stay == null) {
                        JOptionPane.showMessageDialog(null, "Error: Stay not found.");
                        return;
                    }

                    new StayDetailsScreen(stay, currentUser, petRepository, ownerRepository).setVisible(true);

                }
            }
        });
    }

    public void loadStaysIntoTable(StayRepository stayRepository) {
        tableModel.setRowCount(0);

        for (Stay stay : stayRepository.getStayList()) {
            tableModel.addRow(new Object[]{
                    stay.getStayId(),
                    stay.getPetId(),
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
        Comparator<Stay> comparator = null;

        switch (columnIndex) {
            case 0: // Stay ID
                comparator = ascending
                        ? Comparator.comparingInt(Stay::getStayId)
                        : (a, b) -> Integer.compare(b.getStayId(), a.getStayId());
                break;

            case 1: // Pet ID
                comparator = ascending
                        ? Comparator.comparingInt(Stay::getPetId)
                        : (a, b) -> Integer.compare(b.getPetId(), a.getPetId());
                break;

            case 2: // Check-In
                comparator = ascending
                        ? Comparator.comparing(Stay::getCheckInDate)
                        : (a, b) -> b.getCheckInDate().compareTo(a.getCheckInDate());
                break;

            case 3: // Check-Out
                comparator = ascending
                        ? Comparator.comparing(Stay::getCheckOutDate, Comparator.nullsLast(String::compareTo))
                        : (a, b) -> Comparator.nullsLast(String::compareTo)
                        .compare(b.getCheckOutDate(), a.getCheckOutDate());
                break;

            case 4: // Daily Rate
                comparator = ascending
                        ? Comparator.comparingDouble(Stay::getDailyRate)
                        : (a, b) -> Double.compare(b.getDailyRate(), a.getDailyRate());
                break;

            case 5: // Grooming
                comparator = ascending
                        ? Comparator.comparingInt(Stay::getGrooming)
                        : (a, b) -> Integer.compare(b.getGrooming(), a.getGrooming());
                break;

            case 6: // Total Cost
                comparator = ascending
                        ? Comparator.comparingDouble(Stay::getTotalCost)
                        : (a, b) -> Double.compare(b.getTotalCost(), a.getTotalCost());
                break;

            case 7: // Status
                comparator = ascending
                        ? Comparator.comparing(Stay::getStatus)
                        : (a, b) -> b.getStatus().compareTo(a.getStatus());
                break;
        }

        stayRepository.sortStaysBy(comparator);
        loadStaysIntoTable(stayRepository);
    }

    private void updateColumnHeader() {
        String[] columns = {
                "Stay ID", "Pet ID", "Check-In", "Check-Out", "Daily Rate",
                "Grooming", "Total Cost", "Status"
        };

        for (int i = 0; i < columns.length; i++) {
            if (i == lastSortedColumn) {
                staysTable.getColumnModel().getColumn(i).setHeaderValue(
                        columns[i] + (ascending ? " ▲" : " ▼")
                );
            } else {
                staysTable.getColumnModel().getColumn(i).setHeaderValue(columns[i]);
            }
        }

        staysTable.getTableHeader().repaint();
    }
}
