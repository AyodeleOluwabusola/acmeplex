package com.uofc.acmeplex.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "showtime_seats")
@Entity
public class ShowtimeSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id ;

    @ManyToOne
    @JoinColumn(name = "showtime_fk", nullable = false)
    private Showtime showtime;

    @ManyToOne
    @JoinColumn(name = "theatre_seat_fk", nullable = false)
    private TheatreSeat theatreSeat;
}