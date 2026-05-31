package com.petboarding.Repository;

import com.petboarding.Models.Owner;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import com.petboarding.Utilities.SortUtil;

public class OwnerRepository {

    private ArrayList<Owner> ownerList = new ArrayList<>();
    private HashMap<Integer, Owner> ownerMap = new HashMap<>();

    public ArrayList<Owner> getOwnerList() {
        return ownerList;
    }

    public HashMap<Integer, Owner> getOwnerMap() {
        return ownerMap;
    }

    public void addOwner(Owner owner) {
        ownerList.add(owner);
        ownerMap.put(owner.getOwnerId(), owner);
    }

    public Owner getOwnerById(int ownerId) {
        return ownerMap.get(ownerId);
    }

    public void sortOwnersBy(Comparator<Owner> comparator) {
        SortUtil.sort(ownerList, comparator);
    }

}
