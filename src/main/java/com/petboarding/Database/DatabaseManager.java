//Packages
package com.petboarding.Database;

//Imports
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:pets.db";
    private static Connection connection;

    /*
        Get connection to SQLite database
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            initializeDB();
        }
        return connection;
    }

    /*
        Initialize DB with the following SCHEMA if it does not exist already
     */
    private static void initializeDB() throws SQLException {
        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                salt TEXT NOT NULL,
                role TEXT NOT NULL
                );
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(createUsersTable);
        }
    }
}
