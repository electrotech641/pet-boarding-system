//Packages
package com.petboarding.Database;

//Imports
import com.petboarding.Models.User;
import com.petboarding.Utilities.PasswordUtil;
import java.sql.*;

public class UserDAO {

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

    public boolean createUser(String username, String password, String role) throws SQLException {
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);

        String sql = "INSERT INTO users (username, password_hash, salt, role) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, hash);
            statement.setString(3, salt);
            statement.setString(4, role);
            return statement.executeUpdate() > 0;
        }
    }
}
