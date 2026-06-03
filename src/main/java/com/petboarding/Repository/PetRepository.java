//Package
package com.petboarding.Repository;

//Imports
import com.petboarding.Models.Pet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.petboarding.Utilities.SortUtil;

public class PetRepository {
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

    public void removePet(int petId) {
        petList.removeIf(p -> p.getPetId() == petId);       //Most efficient for this data structure
        petMap.remove(petId);
    }

    public List<Pet> getPetsByName(String name) {
        List<Pet> results = new ArrayList<>();
        String search = name.toLowerCase();

        //Built for partial matches as well, to try and capture mispellings
        for (Pet p : petList) {
            if (p.getName().toLowerCase().contains(search)) {
                results.add(p);
            }
        }
        return results;
    }

}
