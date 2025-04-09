package com.easycontract.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String username;
    private List<String> roles;
    
    public LoginResponse(String token, Long id, String username, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
}
