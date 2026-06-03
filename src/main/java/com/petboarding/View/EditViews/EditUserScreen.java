package com.petboarding.View.EditViews;

import com.petboarding.Database.UserDAO;
import com.petboarding.Models.User;
import com.petboarding.Repository.UserRepository;
import com.petboarding.Utilities.PasswordUtil;
import com.petboarding.View.ManageUsersScreen;

import javax.swing.*;
import java.awt.*;

public class EditUserScreen extends JFrame {

    private final User targetUser, currentUser;
    private final UserRepository userRepository;
    private final ManageUsersScreen parent;
    private final UserDAO userDAO = new UserDAO();

    private JTextField usernameField;
    private JComboBox<String> roleDropdown;
    private JPasswordField passwordField;
    private JButton saveButton;

    public EditUserScreen(User currentUser, User targetUser, UserRepository userRepository, ManageUsersScreen parent) {
        this.currentUser = currentUser;
        this.targetUser = targetUser;
        this.userRepository = userRepository;
        this.parent = parent;

        setTitle("Edit User - " + targetUser.getUsername());
        setSize(350, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        usernameField = new JTextField(targetUser.getUsername());
        roleDropdown = new JComboBox<>(new String[]{"ADMIN", "READ_ONLY", "STAFF"});
        roleDropdown.setSelectedItem(targetUser.getRole());

        passwordField = new JPasswordField();
        passwordField.setToolTipText("Leave blank to keep current password");

        saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> saveChanges());

        add(new JLabel("Username:"));
        add(usernameField);

        add(new JLabel("Role:"));
        add(roleDropdown);

        add(new JLabel("New Password:"));
        add(passwordField);

        add(new JLabel());
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
            User updatedUser = userDAO.updateUser(currentUser, targetUser, passwordChanged);

            if (updatedUser != null) {
                JOptionPane.showMessageDialog(this, "User updated successfully");

                // Update repository
                userRepository.updateUser(updatedUser);

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

}
