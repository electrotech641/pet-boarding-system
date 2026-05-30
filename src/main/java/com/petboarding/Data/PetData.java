package com.petboarding.Data;

import com.petboarding.Models.Pet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import com.petboarding.Utilities.SortUtil;

public class PetData {
    private ArrayList<Pet> petList = new ArrayList<>();
    private HashMap<Integer, Pet> petMap = new HashMap<>();

    public ArrayList<Pet> getPetList() {
        return petList;
    }

    public HashMap<Integer, Pet> getPetMap() {
        return petMap;
    }

    public void addPet(Pet pet) {
        petList.add(pet);
        petMap.put(pet.getPetId(), pet);
    }

    public Pet getPetById(int id) {
        return petMap.get(id); // O(1)
    }

    public void sortPetsBy(Comparator<Pet> comparator) {
        SortUtil.sort(petList, comparator);
    }

    /*
        Binary search insertion O(log n)
        Inserts pet into list in correct alphabetical place
     */
    private void insertInOrder(Pet pet) {
        int left = 0;
        int right = petList.size() - 1;
        String newName = pet.getName();

        while (left <= right) {
            int mid = (left + right) / 2;
            String midName = petList.get(mid).getName();

            int compareTo = newName.compareToIgnoreCase(midName);

            if (compareTo > 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        /*
            left is now the correct insertion index
            insert new pet into correct index and shift pets right to make room
         */
        petList.add(left, pet);

        //also add new pet to the hash map
        petMap.put(pet.getPetId(), pet);
    }
}
