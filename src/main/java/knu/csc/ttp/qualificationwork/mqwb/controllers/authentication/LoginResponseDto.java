package knu.csc.ttp.qualificationwork.mqwb.controllers.authentication;

import knu.csc.ttp.qualificationwork.mqwb.entities.user.User;

public class LoginResponseDto {
    private User user;
    private String token;

    public LoginResponseDto(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
