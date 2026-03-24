package com.example.fitnationuser.payment;

import com.example.fitnationuser.user.User;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_type", nullable = false, columnDefinition = "payment_entity_type")
    private String paymentType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(nullable = false, columnDefinition = "payment_status")
    private String status;
}