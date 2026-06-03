//Package
package com.petboarding.Database;

//Imports
import com.petboarding.Repository.OwnerRepository;
import com.petboarding.Models.Owner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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

    public static int addOwner(Owner owner) {
        String sql = "INSERT INTO owners(name, phone, email, address) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, owner.getName());
            statement.setString(2, owner.getPhone());
            statement.setString(3, owner.getEmail());
            statement.setString(4, owner.getAddress());

            statement.executeUpdate();

            try (Statement newStatement = connection.createStatement();
                 ResultSet rs = newStatement.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
