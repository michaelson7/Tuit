package com.nkwazi_tech.tuit_app.Classes;

public class DataHandler_FirebaseUsers {
    private String username;

    public DataHandler_FirebaseUsers(String id, String img, String username) {
        this.username = username;
    }

    public DataHandler_FirebaseUsers() {
    }

    public String getUsername() {
        return username;
    }
}
