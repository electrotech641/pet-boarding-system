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

            String role = user.getRole();

            //Reset all flags first
            user.setAdmin(false);
            user.setStaff(false);
            user.setReadOnly(false);

            //Set correct flag based on role
            switch (role) {
                case "ADMIN":
                    user.setAdmin(true);
                    break;

                case "STAFF":
                    user.setStaff(true);
                    break;

                case "READ_ONLY":
                    user.setReadOnly(true);
                    break;

                default:
                    //handle unexpected roles
                    user.setReadOnly(true);
                    break;
            }
            return user;
        } else {
            return null;
        }
    }

}
