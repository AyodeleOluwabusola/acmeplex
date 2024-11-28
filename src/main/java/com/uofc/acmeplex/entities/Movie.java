package com.uofc.acmeplex.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.time.LocalDate;
import java.util.Set;

@Data
@Table(name = "movie")
@Entity
public class Movie extends BaseEntity{

    @Column(name = "name", nullable = false, unique = true)
    private String movieName;

    @Column(name = "genre")
    private String movieGenre;

    @Column(name = "rating")
    private String movieRating;

    @Column(name = "duration", nullable = false)
    private Integer movieDuration;

    @Column(name = "trailer_url")
    private String movieTrailer;

    @Column(name = "image_url")
    private String movieImageUrl;

    @Column(name = "release_date")
    private LocalDate movieReleaseDate;

    @Column(name = "description", length = 4080)
    private String movieDescription;

    @Column(name = "price", nullable = false)
    private Float moviePrice;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "movie")
    @Cascade(CascadeType.ALL)
    private Set<Showtime> showTimes;
}
