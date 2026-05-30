package com.petboarding.Database;

import com.petboarding.Data.CurrentStays;
import com.petboarding.Models.Stay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CurrentStaysRepository {


    public static void loadCurrentStays(CurrentStays currentStaysData) {
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

                currentStaysData.addStay(stay);
            }

            System.out.println("Loaded " + currentStaysData.getStayList().size() + " current stays.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
