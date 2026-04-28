package com.example.interviewservice.dto.python;

import lombok.Data;

@Data
public class TranscribeRequest {

    private String audioBase64;
    private String languageHint;

}
