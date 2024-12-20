package com.uofc.acmeplex.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uofc.acmeplex.enums.UserTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "acmeplex_user")
@Entity
public class User extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "last_payment_date")
    private LocalDateTime lastPaymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserTypeEnum userType;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;
}
