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
import java.util.List;

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



        buildTableModel();
        userTable = new JTable(tableModel);
        configureColumnWidths();
        loadUsersIntoTable();
        addListeners();





        add(new JScrollPane(userTable), BorderLayout.CENTER);

        // Bottom panel with Add User button
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addUserBtn = new JButton("Add User");

        addUserBtn.addActionListener(e ->
                new CreateUserScreen(userRepository, ManageUsersScreen.this).setVisible(true)
        );

        bottom.add(addUserBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private void buildTableModel() {
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void configureColumnWidths() {
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);  //ID
        userTable.getColumnModel().getColumn(1).setPreferredWidth(50);  //username
        userTable.getColumnModel().getColumn(2).setPreferredWidth(50);  //role
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

    private void openSelectedUser() {
        int viewRow = userTable.getSelectedRow();
        if (viewRow < 0) return;

        int modelRow = userTable.convertRowIndexToModel(viewRow);

        int userId = Integer.parseInt(
                userTable.getModel().getValueAt(modelRow, 0).toString()
        );

        User selected = userRepository.getUserById(userId);
        if (selected != null) {
            //new EditUserScreen(selected, userRepository, this).setVisible(true);
        }
    }

    // Refresh table after add/edit
    public void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        model.setRowCount(0);

        for (User user : userRepository.getUserList()) {
            model.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            });
        }
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
}
