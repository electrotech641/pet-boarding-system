//Packages
package com.petboarding.Database;

//Imports
import com.petboarding.Models.User;
import com.petboarding.Repository.UserRepository;
import com.petboarding.Utilities.PasswordUtil;
import java.sql.*;

public class UserDAO {

    public void createDefaultAdminUser(String username, String rawPassword) throws SQLException {

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(rawPassword, salt);

        String sql = "INSERT INTO users (username, password_hash, salt, role) VALUES (?, ?, ?, 'ADMIN')";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, hash);
            stmt.setString(3, salt);

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                User newUser = new User();
                newUser.setId(rs.getInt(1));
                newUser.setUsername(username);
                newUser.setPasswordHash(hash);
                newUser.setSalt(salt);
                newUser.setRole("ADMIN");
            }
        }

    }

    public static void loadUsers(UserRepository userRepository) {
        String sql = "SELECT * FROM users";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                User user = new User();
                user.setId(results.getInt("id"));
                user.setUsername(results.getString("username"));
                user.setRole(results.getString("role"));

                userRepository.addUser(user);
            }
            System.out.println("Loaded " + userRepository.getUserList().size() + " users into memory.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        Object Database;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User newUser = new User();
                    newUser.setUsername(resultSet.getString("username"));
                    newUser.setPasswordHash(resultSet.getString("password_hash"));
                    newUser.setSalt(resultSet.getString("salt"));
                    newUser.setRole(resultSet.getString("role"));
                    return newUser;
                }
            }
        }
        return null;
    }

    public User createUser(User currentUser, String username, String password, String role) throws SQLException {

        //Check if it is self registration using login screen, no user current logged in
        boolean isSelfRegistration = currentUser == null;

        //Backend check for admin, if non-read-only user being created
        if (!isSelfRegistration && !currentUser.getRole().equals("ADMIN")) {
            throw new SecurityException("Only admins can create users");
        }

        //Self-registration can ONLY create READ_ONLY users
        if (isSelfRegistration && !role.equals("READ_ONLY")) {
            throw new SecurityException("Self-registration can only create READ_ONLY users");
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);

        String sql = "INSERT INTO users (username, password_hash, salt, role) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, username);
            statement.setString(2, hash);
            statement.setString(3, salt);
            statement.setString(4, role);

            int rows = statement.executeUpdate();
            if (rows == 0) {
                return null; // insert failed
            }

            // Retrieve generated user_id
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int userId = keys.getInt(1);

                    User user = new User();
                    user.setId(userId);
                    user.setUsername(username);
                    user.setPasswordHash(hash);
                    user.setSalt(salt);
                    user.setRole(role);

                    return user;
                }
            }
        }

        return null;
    }

    public User updateUser(User currentUser, User targetUser, boolean passwordChanged) throws SQLException {

        //Backend check for admin role
        if (!currentUser.isAdmin()) {
            throw new SecurityException("You do not have permission to modify users.");
        }

        //Set sql statement based on if the password was changed
        String sql;
        if (passwordChanged) {
            sql = "UPDATE users SET username = ?, role = ?, password_hash = ?, salt = ? WHERE id = ?";
        } else {
            sql = "UPDATE users SET username = ?, role = ? WHERE id = ?";
        }

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, targetUser.getUsername());
            statement.setString(2, targetUser.getRole());

            //Update salt and hash if password was changed
            if (passwordChanged) {
                // Hash new password
                String newSalt = PasswordUtil.generateSalt();
                String newHash = PasswordUtil.hashPassword(targetUser.getPasswordHash(), newSalt);

                statement.setString(3, newHash);
                statement.setString(4, newSalt);
                statement.setInt(5, targetUser.getId());

                // Update user object
                targetUser.setPasswordHash(newHash);
                targetUser.setSalt(newSalt);

            } else {
                statement.setInt(3, targetUser.getId());
            }

            int rows = statement.executeUpdate();

            if (rows > 0) {
                return targetUser;
            } else {
                return null;
            }
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            int rows = statement.executeUpdate();
            return rows > 0;
        }
    }



}
