package com.uofc.acmeplex.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.uofc.acmeplex.enums.BookingStatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "ticket")
@Entity
public class Ticket extends BaseEntity {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "user_fk", nullable = false)
    private AcmePlexUser user;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "movie_fk", nullable = false)
    private Movie movie;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "showtime_fk", nullable = false)
    private Showtime showtime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatusEnum bookingStatus = BookingStatusEnum.PENDING; // PENDING, PAID, CANCELLED

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TheatreSeat> reservedSeats; // List of reserved seats for the ticket
}