//Packages
package com.petboarding.Database;

//Imports
import com.petboarding.Repository.StayRepository;
import com.petboarding.Models.Stay;

import java.sql.*;

public class StayDAO {


    public static void loadCurrentStays(StayRepository stayRepository) throws SQLException {

        String sql = "SELECT stay_id, pet_id, check_in, check_out, daily_rate, grooming, total_cost, status " +
                "FROM stays WHERE status = 'In Progress'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Stay stay = new Stay(
                        rs.getInt("stay_id"),
                        rs.getInt("pet_id"),
                        rs.getString("check_in"),
                        rs.getString("check_out"),
                        rs.getDouble("daily_rate"),
                        rs.getInt("grooming"),
                        rs.getDouble("total_cost"),
                        rs.getString("status")
                );

                stayRepository.addStay(stay);
            }

            System.out.println("Loaded " + stayRepository.getStayList().size() + " current stays.");

        }
    }

    public static void updateStay(Stay stay) throws SQLException {
        String sql = "UPDATE stays SET check_in = ?, check_out = ?, daily_rate = ?, grooming = ?, total_cost = ?, status = ? WHERE stay_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, stay.getCheckInDate());
            statement.setString(2, stay.getCheckOutDate());
            statement.setDouble(3, stay.getDailyRate());
            statement.setInt(4, stay.getGrooming());
            statement.setDouble(5, stay.getTotalCost());
            statement.setString(6, stay.getStatus());
            statement.setInt(7, stay.getStayId());

            statement.executeUpdate();
        }
    }


    public static Stay addStay(Stay stay) throws SQLException {

        String sql = "INSERT INTO stays (pet_id, check_in, check_out, daily_rate, grooming, total_cost, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, stay.getPetId());
            stmt.setString(2, stay.getCheckInDate());
            stmt.setString(3, stay.getCheckOutDate());
            stmt.setDouble(4, stay.getDailyRate());
            stmt.setInt(5, stay.getGrooming());
            stmt.setDouble(6, stay.getTotalCost());   // may be 0 for In Progress
            stmt.setString(7, stay.getStatus());

            stmt.executeUpdate();

            // Retrieve generated stay_id
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                stay.setStayId(rs.getInt(1));
                return stay;
            }

            return null;
        }

    }
}
