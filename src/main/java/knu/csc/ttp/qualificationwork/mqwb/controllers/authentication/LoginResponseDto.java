package knu.csc.ttp.qualificationwork.mqwb.controllers.authentication;

import java.util.UUID;

public class LoginResponseDto {
    private UUID id;
    private String token;

    public LoginResponseDto(UUID id, String token) {
        this.id = id;
        this.token = token;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
