package com.uofc.acmeplex.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "invoice")
@Entity
public class Invoice extends BaseEntity{

    @Column(name = "amount", nullable = false)
    private Float amount = 0F;

    @Column(name = "payment_ref", nullable = false)
    private String paymentReference;

    @ManyToOne
    @JoinColumn(name = "payment_fk", nullable = false)
    private Card card;

}
