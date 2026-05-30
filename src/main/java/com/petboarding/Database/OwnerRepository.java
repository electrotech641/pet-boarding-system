package com.petboarding.Database;

import com.petboarding.Data.OwnerData;
import com.petboarding.Models.Owner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OwnerRepository {


    public static void loadOwners(OwnerData ownerData) {
        String sql = "SELECT owner_id, name, phone, email, address FROM owners";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Owner owner = new Owner(
                        rs.getInt("owner_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                );

                ownerData.addOwner(owner);
            }

            System.out.println("Loaded " + ownerData.getOwnerList().size() + " owners.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
