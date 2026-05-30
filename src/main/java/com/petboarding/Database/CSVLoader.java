//Packages
package com.petboarding.Database;

//Imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CSVLoader {

    private static BufferedReader getResourceReader(String resourcePath) throws IOException {
        ClassLoader loader = CSVLoader.class.getClassLoader();
        InputStream inputStream = loader.getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        return new BufferedReader(new InputStreamReader(inputStream));
    }

    public static void loadOwners(String resourcePath) {
        String sql = "INSERT INTO owners (owner_id, name, phone, email, address) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             BufferedReader reader = getResourceReader(resourcePath)) {

            String line;

            // Skip header
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                statement.setInt(1, Integer.parseInt(data[0])); //owner_is
                statement.setString(2, data[1]);                //name
                statement.setString(3, data[2]);                //phone

                // email null check
                if (data[3].isEmpty()) {
                    statement.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    statement.setString(4, data[3]);
                }

                // address null check
                if (data[4].isEmpty()) {
                    statement.setNull(5, java.sql.Types.VARCHAR);
                } else {
                    statement.setString(5, data[4]);
                }

                statement.addBatch();
            }

            statement.executeBatch();
            System.out.println("Owners loaded successfully.");

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadPets(String resourcePath) {
        String sql = "INSERT INTO pets (pet_id, owner_id, name, species, age, notes) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             BufferedReader reader = getResourceReader(resourcePath)) {

            String line;

            // Skip header
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                statement.setInt(1, Integer.parseInt(data[0]));         //pet_id
                statement.setInt(2, Integer.parseInt(data[1]));         //owner_id
                statement.setString(3, data[2]);                        //name
                statement.setString(4, data[3]);                        //species

                //age null check
                if (data[4].isEmpty()) {
                    statement.setNull(5, java.sql.Types.INTEGER);
                } else {
                    statement.setInt(5, Integer.parseInt(data[4]));     //age
                }
                // notes null check
                if (data.length > 5 && !data[5].isEmpty()) {
                    statement.setString(6, data[5]);                    //notes
                } else {
                    statement.setNull(6, java.sql.Types.VARCHAR);
                }

                statement.addBatch();
            }

            statement.executeBatch();
            System.out.println("Pets loaded successfully.");

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadStays(String resourcePath) {
        String sql = "INSERT INTO stays (stay_id, pet_id, check_in, check_out, daily_rate, grooming, total_cost, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             BufferedReader reader = getResourceReader(resourcePath)) {

            String line;

            // Skip header
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                statement.setInt(1, Integer.parseInt(data[0]));             // stay_id
                statement.setInt(2, Integer.parseInt(data[1]));             // pet_id
                statement.setString(3, data[2]);                            // check_in

                // check_out null check
                if (data[3].isEmpty()) {
                    statement.setNull(4, java.sql.Types.VARCHAR);
                } else {
                    statement.setString(4, data[3]);                        //check_out
                }

                statement.setDouble(5, Double.parseDouble(data[4]));        // daily_rate
                statement.setInt(6, Integer.parseInt(data[5]));             // grooming

                // total_cost null check
                if (data[6].isEmpty()) {
                    statement.setNull(7, java.sql.Types.REAL);
                } else {
                    statement.setDouble(7, Double.parseDouble(data[6]));    //total_cost
                }

                statement.setString(8, data[7]);                            // status

                statement.addBatch();
            }

            statement.executeBatch();
            System.out.println("Stays loaded successfully.");

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}

