package com.petboarding.Models;

public class User {

    private int id;
    private String username;
    private String passwordHash;
    private String salt;
    private String role;
    private boolean isAdmin, isStaff, isReadOnly;

    //Default constructor
    public User() {}

    public User(int id, String username, String passwordHash, String salt, String role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;

        if (role.equals("ADMIN")) {
            this.isAdmin = true;
        }
        else if (role.equals("STAFF")) {
            this.isStaff = true;
        }
        else if (role.equals("READ_ONLY")) {
            this.isReadOnly = true;
        }
    }

    /*
        Getters and setters for User attributes
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isStaff() {
        return isStaff;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setStaff(boolean isStaff) {
        this.isStaff = isStaff;
    }

    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

}
