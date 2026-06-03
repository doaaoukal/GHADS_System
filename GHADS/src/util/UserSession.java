/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import model.User;

public class UserSession {
    
    private static UserSession instance;
    private User loggedInUser;

    // Suppress default constructor to enforce the strict Singleton constraint
    private UserSession() {}

    /**
     * Global access point for the session container.
     */
    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    /**
     * Clears the current user data from memory during logout.
     */
    public void cleanUserSession() {
        this.loggedInUser = null;
    }

    /**
     * Helper method to instantly check if the logged-in user is an Administrator.
     * Useful for UI view access controls inside various Controllers.
     */
    public boolean isAdmin() {
        return loggedInUser != null && "ADMIN".equalsIgnoreCase(loggedInUser.getRole());
    }
}