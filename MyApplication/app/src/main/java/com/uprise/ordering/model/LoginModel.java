package com.uprise.ordering.model;

/**
 * Created by cicciolina on 11/12/16.
 */

public class LoginModel {

    private String token;
    private String username;
    private String password;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
