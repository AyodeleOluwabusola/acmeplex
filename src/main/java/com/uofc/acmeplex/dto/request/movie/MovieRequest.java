package com.uofc.acmeplex.dto.request.movie;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.uofc.acmeplex.entities.Movie;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MovieRequest {

    @NotNull(message = "Movie title is required")
    private String title;
    private String description;
    private String genre;

    @NotNull(message = "Movie duration is required")
    private Integer durationInMinutes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate releaseDate;
    private String rating;
    private String trailerUrl;

    @NotNull(message = "Movie price is required")
    private Float price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:MM:ss")
    private List<LocalDateTime> showTimes;

    public static Movie convertToEntity(MovieRequest movieRequest) {
        Movie movie = new Movie();
        movie.setMovieName(movieRequest.getTitle());
        movie.setMovieDescription(movieRequest.getDescription());
        movie.setMoviePrice(movieRequest.getPrice());
        movie.setMovieGenre(movieRequest.getGenre());
        movie.setMovieDuration(movieRequest.getDurationInMinutes());
        movie.setMovieReleaseDate(movieRequest.getReleaseDate());
        movie.setMovieTrailer(movieRequest.getTrailerUrl());
        movie.setMovieRating(movieRequest.getRating());
        return movie;
    }
}
