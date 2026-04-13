package com.example.fitnationcommon.dto.request;

import com.example.fitnationcommon.constants.ApplicationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
        @NotBlank(message = ApplicationConstants.MESSAGE_BODY_NOT_BLANK)
        @Size(max = ApplicationConstants.CHAT_MESSAGE_MAX_LENGTH, message = ApplicationConstants.MESSAGE_TOO_LONG)
        String body
) {}