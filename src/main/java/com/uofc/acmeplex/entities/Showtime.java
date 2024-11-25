package com.uofc.acmeplex.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Table(name = "show_times")
@Entity
public class Showtime extends BaseEntity {

    /** Junction table between Theatre and Movie and Showtime
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
}