package com.example.interviewservice.dto.python;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TranscribeResponse {

    private String text;
    private String language;

    @JsonProperty("duration_seconds")
    private Double durationSeconds;

    private List<Segment> segments;

    private String model;

    @Data
    public static class Segment {
        private Double start;
        private Double end;
        private String text;
    }
}
