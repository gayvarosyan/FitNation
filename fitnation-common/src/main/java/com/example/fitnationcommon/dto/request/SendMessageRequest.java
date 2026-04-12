package com.example.fitnationcommon.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
        @NotBlank(message = "Message body cannot be blank")
        @Size(max = 8000, message = "Message too long")
        String body
) {}