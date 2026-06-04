//Package
package com.petboarding.View.EditViews;

//Imports
import com.petboarding.Database.UserDAO;
import com.petboarding.Models.User;
import com.petboarding.Utilities.PasswordUtil;
import com.petboarding.View.AppContext;
import com.petboarding.View.ManageUsersScreen;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class EditUserScreen extends JFrame {

    private final User targetUser;
    private final ManageUsersScreen parent;
    private final UserDAO userDAO = new UserDAO();
    private final AppContext context;

    private final JTextField usernameField;
    private final JComboBox<String> roleDropdown;
    private final JPasswordField passwordField;

    public EditUserScreen(AppContext context, User targetUser, ManageUsersScreen parent) {
        this.context = context;
        this.targetUser = targetUser;
        this.parent = parent;

        setTitle("Edit User - " + targetUser.getUsername());
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        usernameField = new JTextField(targetUser.getUsername());
        roleDropdown = new JComboBox<>(new String[]{"ADMIN", "READ_ONLY", "STAFF"});
        roleDropdown.setSelectedItem(targetUser.getRole());

        passwordField = new JPasswordField();
        passwordField.setToolTipText("Leave blank to keep current password");

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> saveChanges());
        JButton deleteButton = new JButton("Delete User");
        deleteButton.addActionListener(e -> {
            try {
                deleteUser();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        add(new JLabel("Username:"));
        add(usernameField);

        add(new JLabel("Role:"));
        add(roleDropdown);

        add(new JLabel("New Password:"));
        add(passwordField);

        add(deleteButton);
        add(saveButton);
    }

    private void saveChanges() {
        String newUsername = usernameField.getText().trim();
        String newRole = roleDropdown.getSelectedItem().toString();
        String newPassword = new String(passwordField.getPassword());

        if (newUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty");
            return;
        }

        try {
            boolean passwordChanged = !newPassword.isEmpty();

            // If password changed, validate + hash
            if (passwordChanged) {
                String validationMessage = PasswordUtil.getPasswordValidationMessage(newPassword);
                if (validationMessage != null) {
                    JOptionPane.showMessageDialog(this, validationMessage);
                    return;
                }
            }

            targetUser.setUsername(newUsername);
            targetUser.setRole(newRole);

            //Store new password temporarily in hashed password IF the password was changed
            if (passwordChanged) {
                targetUser.setPasswordHash(newPassword);
            }

            //Update user in DB
            User updatedUser = userDAO.updateUser(context.currentUser, targetUser, passwordChanged);

            if (updatedUser != null) {
                JOptionPane.showMessageDialog(this, "User updated successfully");

                // Update repository
                context.userRepository.updateUser(updatedUser);
                parent.refreshTable();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update user");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating user");
        }
    }

    private void deleteUser() throws SQLException {
        //Show confirmation dialog to confirm deletion
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this user?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return; // User canceled
        }

        //User confirmed, try to delete user from DB then repository
        try {
            boolean success = userDAO.deleteUser(targetUser.getId());

            if (!success) {
                JOptionPane.showMessageDialog(this, "Failed to delete user from database", "Delete Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            context.userRepository.removeUserById(targetUser.getId());
            dispose();
            parent.refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Delete Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

}
