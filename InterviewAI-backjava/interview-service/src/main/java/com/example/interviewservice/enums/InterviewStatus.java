package com.example.interviewservice.enums;

public enum InterviewStatus {

    CREATED,        // интервью создано, но вопросы ещё не заданы
    IN_PROGRESS,    // интервью идёт
    FINISHED,       // интервью завершено
    CANCELLED       // отменено пользователем / системой

}
