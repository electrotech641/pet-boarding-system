package com.petboarding.Database;

import com.petboarding.Models.Pet;
import com.petboarding.Repository.PetRepository;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PetDAO {

    /*
        Load pets into memory
     */
    public static void loadPets(PetRepository petRepository) {
        String sql = "SELECT * FROM pets";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                Pet pet = new Pet(
                        results.getInt("pet_id"),
                        results.getInt("owner_id"),
                        results.getString("name"),
                        results.getString("species"),
                        results.getInt("age"),
                        results.getString("notes")
                );

                petRepository.addPet(pet);
            }

            System.out.println("Loaded " + petRepository.getPetList().size() + " pets into memory.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        Get a pets stay history by ID
     */
    public static List<String> getStayHistory(int petId) {
        List<String> stayHistory = new ArrayList<>();

        String sql = "SELECT check_in, check_out, grooming FROM stays WHERE pet_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, petId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String grooming;
                if (rs.getInt("grooming") == 1) {
                    grooming = "yes";
                } else {
                    grooming = "no";
                }

                String entry = "In: " + rs.getString("check_in") +
                        " | Out: " + rs.getString("check_out") + " | Grooming: " + grooming;
                stayHistory.add(entry);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stayHistory;
    }

    public static void updatePet(Pet pet) {
        String sql = "UPDATE pets SET name = ?, species = ?, age = ?, notes = ? WHERE pet_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pet.getName());
            statement.setString(2, pet.getSpecies());
            statement.setInt(3, pet.getAge());
            statement.setString(4, pet.getNotes());
            statement.setInt(5, pet.getPetId());

            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPetCurrentlyBoarded(int petId) {
        String sql = "SELECT COUNT(*) FROM stays " +
                "WHERE pet_id = ? AND status = 'In Progress'";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, petId);

            //Check for a result, return true if one exists
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Query returned no results, return false, pet currently not boarded
        return false;

    }
}
