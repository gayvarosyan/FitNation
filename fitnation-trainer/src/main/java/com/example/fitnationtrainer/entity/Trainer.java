package com.example.fitnationtrainer.entity;

import com.example.fitnationcommon.constants.ApplicationConstants;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import com.example.fitnationuser.user.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "trainers")
@PrimaryKeyJoinColumn(name = "user_id")
@Access(AccessType.FIELD)
@EqualsAndHashCode(callSuper = true)
public class Trainer extends User {
    @Column(nullable = false, length = ApplicationConstants.SMALL_TEXT)
    private String specialization;

    @Column(length = ApplicationConstants.LARGE_TEXT)
    private String bio;
}