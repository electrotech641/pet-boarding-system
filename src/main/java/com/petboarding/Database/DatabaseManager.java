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
        String[] createSchema = {
                "PRAGMA foreign_keys = ON",

                """
                CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                salt TEXT NOT NULL,
                role TEXT NOT NULL
                )
                """,

                """
                CREATE TABLE IF NOT EXISTS owners (
                owner_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                phone TEXT NOT NULL,
                email TEXT UNIQUE,
                address TEXT
                )
                """,

                """
                CREATE TABLE IF NOT EXISTS pets (
                pet_id INTEGER PRIMARY KEY AUTOINCREMENT,
                owner_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                species TEXT NOT NULL,
                age INTEGER,
                notes TEXT,
                FOREIGN KEY (owner_id)
                    REFERENCES owners(owner_id)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE
                )
                """,

                """
                CREATE TABLE IF NOT EXISTS stays (
                stay_id INTEGER PRIMARY KEY AUTOINCREMENT,
                pet_id INTEGER NOT NULL,
                check_in TEXT NOT NULL,
                check_out TEXT,
                daily_rate REAL NOT NULL,
                grooming INTEGER NOT NULL DEFAULT 0, --0 = no grooming, 1 = grooming requested
                total_cost REAL,
                status TEXT NOT NULL,
                FOREIGN KEY (pet_id)
                    REFERENCES pets(pet_id)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE
                )
                """,

                """
                CREATE TABLE IF NOT EXISTS bills (
                    bill_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    stay_id INTEGER NOT NULL,
                    amount REAL NOT NULL,
                    issued_date TEXT NOT NULL,
                    paid_date TEXT,
                    status TEXT NOT NULL,
                    FOREIGN KEY (stay_id)
                        REFERENCES stays(stay_id)
                        ON DELETE CASCADE
                        ON UPDATE CASCADE
                )
                """
        };
        try (Statement statement = connection.createStatement()) {
            for (String sql : createSchema) {
                statement.execute(sql);
            }
        }
    }
}
