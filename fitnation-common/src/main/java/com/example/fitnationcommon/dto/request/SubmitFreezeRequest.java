package com.example.fitnationcommon.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SubmitFreezeRequest(

    @NotNull
    LocalDate freezeStart,

    @NotNull
    LocalDate freezeEnd) {

}