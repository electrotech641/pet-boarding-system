//Package
package com.petboarding.Repository;

//Imports
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import com.petboarding.Models.Stay;
import com.petboarding.Utilities.SortUtil;

public class StayRepository {

    private ArrayList<Stay> stayList = new ArrayList<>();
    private HashMap<Integer, Stay> stayMap = new HashMap<>();

    public ArrayList<Stay> getStayList() {
        return stayList;
    }


    public void addStay(Stay stay) {
        insertInOrder(stay);
        stayMap.put(stay.getStayId(), stay);
    }

    public Stay getStayById(int stayId) {
        return stayMap.get(stayId);
    }

    public void sortStaysBy(Comparator<Stay> comparator) {
        SortUtil.sort(stayList, comparator);
    }

    /*
        Binary insertion for stays, by check in date in ascending order for initial stays table view
        This will initially populate the current stays table with the oldest check in dates at the top
     */
    private void insertInOrder(Stay stay) {
        int left = 0;
        int right = stayList.size() - 1;

        // Parse the new stay's check-in date
        String newDate = stay.getCheckInDate();

        while (left <= right) {
            int mid = (left + right) / 2;

            String midDate = stayList.get(mid).getCheckInDate();

            int compare = newDate.compareTo(midDate);

            if (compare > 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        // Insert at the correct sorted position
        stayList.add(left, stay);
    }

    public void removeStay(int stayId) {
        stayList.removeIf(s -> s.getStayId() == stayId);        //Most efficient for this data structure
        stayMap.remove(stayId);
    }

    public void updateStay(Stay updateStay) {
        for (int i = 0; i < stayList.size(); i++) {
            if (stayList.get(i).getStayId() == updateStay.getStayId()) {
                stayList.set(i, updateStay);
                return;
            }
        }
    }

}
