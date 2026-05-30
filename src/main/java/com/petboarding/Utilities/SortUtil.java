package com.petboarding.Utilities;

import com.petboarding.Models.Pet;
import java.util.ArrayList;
import java.util.Comparator;

public class SortUtil {

    /*
        Public sort method for merge sorting, all else kept private to this class
     */
    public static <T> void sort(ArrayList<T> list, Comparator<T> comparator) {
        if (list == null || list.size() <= 1) return;
        mergeSort(list, 0, list.size() - 1, comparator);
    }

    private static <T> void mergeSort(ArrayList<T> list, int left, int right, Comparator<T> comparator) {
        if (left >= right) return;

        int mid = (left + right) / 2;

        mergeSort(list, left, mid, comparator);
        mergeSort(list, mid + 1, right, comparator);

        merge(list, left, mid, right, comparator);
    }

    private static <T> void merge(ArrayList<T> list, int left, int mid, int right, Comparator<T> comparator) {
        ArrayList<T> temp = new ArrayList<>();

        int i = left;
        int j = mid + 1;

        while (i <= mid && j <= right) {
            if (comparator.compare(list.get(i), list.get(j)) <= 0) {
                temp.add(list.get(i));
                i++;
            } else {
                temp.add(list.get(j));
                j++;
            }
        }

        while (i <= mid) {
            temp.add(list.get(i));
            i++;
        }

        while (j <= right) {
            temp.add(list.get(j));
            j++;
        }

        for (int k = 0; k < temp.size(); k++) {
            list.set(left + k, temp.get(k));
        }
    }
}
