package com.petboarding.View;

import com.petboarding.Models.User;
import com.petboarding.Repository.UserRepository;
import com.petboarding.View.CreateViews.CreateUserScreen;
import com.petboarding.View.EditViews.EditUserScreen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ManageUsersScreen extends JFrame {

    private JTable userTable;
    private DefaultTableModel tableModel;
    private UserRepository userRepository;
    private User currentUser;

    private final String[] cols = {"User ID", "Username", "Role"};

    public ManageUsersScreen(UserRepository userRepository, User currentUser) {
        this.userRepository = userRepository;
        this.currentUser = currentUser;

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
                new CreateUserScreen(currentUser, userRepository, this).setVisible(true)
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

        for (User user : userRepository.getUserList()) {
            tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            });
        }
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

        User selectedUser = userRepository.getUserById(userId);
        if (selectedUser != null) {
            new EditUserScreen(currentUser, selectedUser, userRepository, this).setVisible(true);
        }
    }

    // Refresh table after add/edit
    public void refreshTable() {
        tableModel.setRowCount(0);

        for (User user : userRepository.getUserList()) {
            tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            });
        }
    }


}
