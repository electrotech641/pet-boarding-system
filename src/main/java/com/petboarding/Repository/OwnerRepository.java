//Package
package com.petboarding.Repository;

//Imports
import com.petboarding.Models.Owner;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.petboarding.Utilities.SortUtil;

public class OwnerRepository {

    private final ArrayList<Owner> ownerList = new ArrayList<>();
    private final HashMap<Integer, Owner> ownerMap = new HashMap<>();

    public ArrayList<Owner> getOwnerList() {
        return ownerList;
    }

    public void addOwner(Owner owner) {
        ownerList.add(owner);
        ownerMap.put(owner.getOwnerId(), owner);
    }

    public Owner getOwnerById(int ownerId) {
        return ownerMap.get(ownerId);
    }

    public String getOwnerNameById(int ownerId) {
        Owner owner = ownerMap.get(ownerId);
        return owner != null ? owner.getName() : "Unknown";
    }

    public void sortOwnersBy(Comparator<Owner> comparator) {
        SortUtil.sort(ownerList, comparator);
    }

    public void removeOwnerById(int ownerId) {
        ownerList.removeIf(owner -> owner.getOwnerId() == ownerId);     //Most efficient for this data structure
        ownerMap.remove(ownerId);
    }

    public List<Owner> getOwnersByName(String name) {
        List<Owner> results = new ArrayList<>();
        String search = name.toLowerCase();

        for (Owner owner : ownerList) {
            if (owner.getName().toLowerCase().contains(search)) {
                results.add(owner);
            }
        }

        return results;
    }

}
