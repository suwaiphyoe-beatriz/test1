package controller;

import model.User;

public class SessionManager {
    
    private static SessionManager instance;
    private User currentUser;
    
    private SessionManager() {
    }
    
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean isTeacher() {
        return currentUser != null && currentUser.isTeacher();
    }
    
    public boolean isLearner() {
        return currentUser != null && currentUser.isLearner();
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }
}
