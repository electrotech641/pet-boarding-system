package com.petboarding.Data;

import com.petboarding.Models.Owner;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import com.petboarding.Utilities.SortUtil;

public class OwnerData {

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

    /*
        Binary insertion for owner list
     */
    private void insertInOrder(Owner owner) {
        int left = 0;
        int right = ownerList.size() - 1;
        String newName = owner.getName();

        while (left <= right) {
            int mid = (left + right) / 2;
            String midName = ownerList.get(mid).getName();

            int compareTo = newName.compareToIgnoreCase(midName);

            if (compareTo > 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
    }
}
