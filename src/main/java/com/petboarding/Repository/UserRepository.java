package com.petboarding.Repository;

import com.petboarding.Models.User;
import com.petboarding.Utilities.SortUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class UserRepository {

    private ArrayList<User> userList = new ArrayList<>();
    private HashMap<Integer, User> userMap = new HashMap<>();

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void addUser(User user) {
        userList.add(user);
        userMap.put(user.getId(), user);
    }

    public User getUserById(int userId) {
        return userMap.get(userId);
    }

    public void sortUsersBy(Comparator<User> comparator) {
        SortUtil.sort(userList, comparator);
    }

    public void removeUserById(int userId) {
        userMap.remove(userId);
        userList.removeIf(user -> user.getId() == userId);
    }

    public void updateUser(User user) {
        userMap.put(user.getId(), user);

        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getId() == user.getId()) {
                userList.set(i, user);
                break;
            }
        }
    }
}
