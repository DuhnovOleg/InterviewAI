package com.example.interviewservice.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserContextService {

    public boolean isAuthenticated() {
        return false;
    }

    public UUID getCurrentUserIdOrNull() {
        return null;
    }
}
