package com.uofc.acmeplex.entities;

import com.uofc.acmeplex.enums.CardTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "payment")
@Entity
public class Payment extends BaseEntity{

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private CardTypeEnum cardType;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "card_holder_name", nullable = false)
    private String cardHolderName;

    @Column(name = "expiry_date", nullable = false)
    private String expiryDate;

    @Column(name = "cvv", nullable = false)
    private String cvv;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AcmePlexUser user;

}
