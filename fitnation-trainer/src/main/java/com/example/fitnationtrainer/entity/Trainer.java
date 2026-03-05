package com.example.fitnationtrainer.entity;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationuser.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "trainers")
@PrimaryKeyJoinColumn(name = "user_id")
@EqualsAndHashCode(callSuper = true)
public class Trainer extends User {

    @Column(nullable = false, length = ApplicationConstants.SMALL_TEXT)
    private String specialization;

    @Column(length = ApplicationConstants.LARGE_TEXT)
    private String bio;
}
