package com.example.fitnationcommon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrorDetail {

    private String field;
    private Object rejectedValue;
    private String message;
}