package com.uofc.acmeplex.entities;

import com.uofc.acmeplex.enums.BookingStatusEnum;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "ticket")
@Entity
public class Ticket extends BaseEntity {

    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "showtime_fk", nullable = false)
    private Showtime showtime;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatusEnum bookingStatus = BookingStatusEnum.PENDING; // PENDING, PAID, CANCELLED

    @ElementCollection
    @CollectionTable(name = "ticket_seats", joinColumns = @JoinColumn(name = "ticket_fk"))
    @Column(name = "seats_fk")
    private List<Long> ticketSeats;
}