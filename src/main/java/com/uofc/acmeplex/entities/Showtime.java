package com.uofc.acmeplex.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Table(name = "show_times")
@Entity
public class Showtime extends BaseEntity {

    /** Junction table between Theatre and Movie and ShowtimeP
     * This table will store the show date and time for a movie in a theatre
     */
    @ManyToOne
    @JoinColumn(name = "theatre_fk", nullable = false)
    private Theatre theatre;

    @ManyToOne
    @JoinColumn(name = "movie_fk", nullable = false)
    private Movie movie;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "showtime_seats", joinColumns = @JoinColumn(name = "showtime_fk", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "theatre_seat_fk", referencedColumnName = "id"))
    @Cascade({CascadeType.ALL})
    private Set<TheatreSeat> theatreSeats; //tells if a seat is booked or not for a showtime
}