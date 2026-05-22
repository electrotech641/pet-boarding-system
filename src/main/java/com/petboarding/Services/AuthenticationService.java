//Packages
package com.petboarding.Services;

//Imports
import com.petboarding.Database.UserDAO;
import com.petboarding.Models.User;
import com.petboarding.Utilities.PasswordUtil;
import java.sql.SQLException;

public class AuthenticationService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password) throws SQLException {
        User user = userDAO.findByUsername(username);

        //If user does not exist in users table, return null
        if (user == null) {
            return null;
        }

        /*
            Check password and return User if valid, otherwise return null
         */
        boolean valid = PasswordUtil.verifyPassword(password, user.getSalt(), user.getPasswordHash());

        if (valid) {
            return user;
        } else {
            return null;
        }
    }
}
