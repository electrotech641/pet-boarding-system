package com.petboarding.Repository;

import com.petboarding.Models.Pet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

}
