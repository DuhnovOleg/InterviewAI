package com.example.authservice.dto.respronse;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class RegisterResponse {

    private UUID userId;
    private String email;
    private String username;
    private Set<String> roles;
    private String status;

}
