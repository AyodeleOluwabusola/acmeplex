package com.uofc.acmeplex.repository;

import com.uofc.acmeplex.entities.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByMovieName(String name);

    @Query("SELECT m FROM Movie m WHERE m.active = :active  AND (:movieName IS NULL OR m.movieName = :movieName) AND m.createDate <= :createDate")
    Page<Movie> findAllByActiveAndOptionalMovieNameAndCreateDateLessThanEqual(Pageable pageable, boolean active, String movieName, LocalDateTime createDate);

    @Query("SELECT m FROM Movie m WHERE m.active = :active AND (:movieName IS NULL OR m.movieName = :movieName)")
    Page<Movie> findAllByActiveAndMovieName(Pageable id, boolean active, String movieName);
}
