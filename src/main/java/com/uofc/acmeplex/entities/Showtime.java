package com.uofc.acmeplex.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
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

    @OneToMany(mappedBy = "showtime", fetch = FetchType.LAZY, orphanRemoval = true)
    @Cascade({CascadeType.ALL})
    private Set<ShowtimeSeat> showtimeSeats;

}