package com.uofc.acmeplex.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Table(name = "promo_code")
@Entity
public class PromotionCode extends BaseEntity{

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "amount", nullable = false)
    private Float amount = 0F;

    @Column(name = "balance", nullable = false)
    private Float balance = 0F;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @ManyToOne
    @JoinColumn(name = "user_fk")
    private AcmePlexUser createdBy;
}
