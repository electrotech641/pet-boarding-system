//Package
package com.petboarding.View;

//Imports
import com.petboarding.Models.User;
import com.petboarding.View.CreateViews.CreateUserScreen;
import com.petboarding.View.EditViews.EditUserScreen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;

public class ManageUsersScreen extends JFrame {

    private JTable userTable;
    private DefaultTableModel tableModel;
    private final AppContext context;


    private final String[] cols = {"User ID", "Username", "Role"};

    private int lastSortedModelColumn = -1;
    private boolean ascending = true;

    public ManageUsersScreen(AppContext context) {
        this.context = context;

        setTitle("Manage Users");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //Build table to add into scoll pane
        buildTableModel();
        userTable = new JTable(tableModel);   // must be created BEFORE configuring widths
        configureColumnWidths();
        loadUsersIntoTable();
        addListeners();

        //Bottom panel with Create + Edit buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addUserBtn = new JButton("Create User");
        JButton editUserBtn = new JButton("Edit User");
        bottom.add(addUserBtn);
        bottom.add(editUserBtn);

        //Button click listeners
        addUserBtn.addActionListener(e ->
                new CreateUserScreen(context, this).setVisible(true)
        );

        editUserBtn.addActionListener(e -> openSelectedUser());

        //Construct layout
        add(new JScrollPane(userTable), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    /*
        -----------------Table building and data loading methods-----------------------------
     */
    private void buildTableModel() {
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void configureColumnWidths() {
        userTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Username
        userTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Role
    }

    private void addListeners() {

        userTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewColumn = userTable.columnAtPoint(e.getPoint());
                if (viewColumn < 0) return;

                int modelColumn = userTable.convertColumnIndexToModel(viewColumn);

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
                String colName = userTable.getColumnName(viewColumn);

                context.statusLabel.setText(
                        "Sorted by " + colName + " (" + direction + ") in " + String.format("%.3f ms", ms)
                );
            }
        });

        // Double-click listener
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedUser();
                }
            }
        });
    }

    public void loadUsersIntoTable() {
        tableModel.setRowCount(0);

        for (User user : context.userRepository.getUserList()) {
            tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            });
        }

        System.out.println("UserRepository size = " + context.userRepository.getUserList().size());
    }

    /*
        ----------------Sorting functions------------------------------
     */

    private void sortByColumn(int columnIndex) {
        Comparator<User> comparator = getComparator(columnIndex);

        if (comparator == null) return;
        if (!ascending) comparator = comparator.reversed();

        context.userRepository.sortUsersBy(comparator);
        loadUsersIntoTable();
    }

    private Comparator<User> getComparator(int columnIndex) {
        switch (columnIndex) {
            case 0: return Comparator.comparingInt(User::getId);
            case 1: return Comparator.comparing(user -> user.getUsername());
            case 2: return Comparator.comparing(user -> user.getRole());
            default: return null;
        }
    }

    private void updateColumnHeader() {
        for (int viewIndex = 0; viewIndex < userTable.getColumnCount(); viewIndex++) {
            TableColumn column = userTable.getColumnModel().getColumn(viewIndex);

            int modelIndex = userTable.convertColumnIndexToModel(viewIndex);

            String baseHeader = cols[modelIndex];

            if (modelIndex == lastSortedModelColumn) {
                baseHeader += ascending ? " ▲" : " ▼";
            }

            column.setHeaderValue(baseHeader);
        }

        userTable.getTableHeader().repaint();
    }

    /*
        -----------------Other functions--------------------------------
     */
    private void openSelectedUser() {
        int viewRow = userTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user first");
            return;
        }

        int modelRow = userTable.convertRowIndexToModel(viewRow);

        int userId = Integer.parseInt(
                userTable.getModel().getValueAt(modelRow, 0).toString()
        );

        User selectedUser = context.userRepository.getUserById(userId);
        if (selectedUser != null) {
            new EditUserScreen(context, selectedUser, this).setVisible(true);
        }
    }

    // Refresh table after add/edit
    public void refreshTable() {
        tableModel.setRowCount(0);

        for (User user : context.userRepository.getUserList()) {
            tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            });
        }
    }


}
