package com.plasturgie.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    @JsonProperty("access_token")
    private String token;
    
    @JsonProperty("token_type")
    private String type = "Bearer";
    
    @JsonProperty("user_id")
    private Long userId;
    
    private String username;
    private String email;
    private String role;
    
    public AuthResponse(String token, Long userId, String username, String email, String role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role != null ? role.toUpperCase() : "LEARNER";
    }
}