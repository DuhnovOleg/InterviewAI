package com.example.interviewservice.service;

import com.example.interviewservice.model.GuestInterviewState;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GuestSessionStore {

    private final Map<String, GuestInterviewState> sessions = new ConcurrentHashMap<>();
    private final Map<String, Instant> timestamps = new ConcurrentHashMap<>();

    public void save(String localSessionId, GuestInterviewState state) {
        sessions.put(localSessionId, state);
        timestamps.put(localSessionId, Instant.now());
    }

    public GuestInterviewState get(String localSessionId) {
        return sessions.get(localSessionId);
    }

    public void remove(String localSessionId) {
        sessions.remove(localSessionId);
        timestamps.remove(localSessionId);
    }

}
