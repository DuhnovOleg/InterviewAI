package com.example.authservice.security;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class AuthenticatedUserPrincipal {

    private UUID userId;
    private String email;
    private String username;
    private Set<String> roles;

}
