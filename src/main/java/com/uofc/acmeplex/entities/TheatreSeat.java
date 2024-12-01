package com.uofc.acmeplex.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "theatre_seats")
@Entity
public class TheatreSeat extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "theatre_fk", nullable = false)
    private Theatre theatre;

    @Column(name = "seat_row", nullable = false)
    private String seatRow; // A, B, C, D, E, F, G, H, I

    @Column(name = "seat_number", nullable = false)
    private int seatNumber; // 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
}