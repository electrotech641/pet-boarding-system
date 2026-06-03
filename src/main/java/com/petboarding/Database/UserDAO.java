//Packages
package com.petboarding.Database;

//Imports
import com.petboarding.Models.User;
import com.petboarding.Repository.UserRepository;
import com.petboarding.Utilities.PasswordUtil;
import java.sql.*;

public class UserDAO {

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

    public User createUser(String username, String password, String role) throws SQLException {

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

}
