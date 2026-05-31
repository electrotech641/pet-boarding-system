package com.petboarding.Database;

import com.petboarding.Repository.OwnerRepository;
import com.petboarding.Models.Owner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OwnerDAO {


    public static void loadOwners(OwnerRepository ownerRepository) {
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

                ownerRepository.addOwner(owner);
            }

            System.out.println("Loaded " + ownerRepository.getOwnerList().size() + " owners.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
