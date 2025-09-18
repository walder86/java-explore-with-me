package ru.practicum.base.exception.error;

import lombok.Getter;
import ru.practicum.base.mapper.DateTimeMapper;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private String status;
    private String reason;
    private String message;
    private String timestamp;

    public ErrorResponse(String status, String reason, String message) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = DateTimeMapper.toStringDate(LocalDateTime.now());
    }
}
