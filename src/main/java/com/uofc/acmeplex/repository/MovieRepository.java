package com.uofc.acmeplex.repository;

import com.uofc.acmeplex.entities.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByMovieName(String name);
    Page<Movie> findAllByActiveAndMovieNameAndCreateDateLessThanEqual(Pageable id, boolean active, String movieName, LocalDateTime valid);
    Page<Movie> findAllByActiveAndMovieName(Pageable id, boolean active, String movieName);
}
