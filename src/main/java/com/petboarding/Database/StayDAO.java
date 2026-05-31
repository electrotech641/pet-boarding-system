//Packages
package com.petboarding.Database;

//Imports
import com.petboarding.Repository.StayRepository;
import com.petboarding.Models.Stay;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StayDAO {


    public static void loadCurrentStays(StayRepository stayRepository) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateStay(Stay stay) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
