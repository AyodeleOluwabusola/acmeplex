package com.uofc.acmeplex.repository;

import com.uofc.acmeplex.entities.Showtime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShowTimeRepository extends JpaRepository<Showtime, Long> {

    Optional<Showtime> findByIdAndTheatreId(Long movieId, Long theatreId);
    Page<Showtime> findAllByMovieIdAndTheatreId(Pageable pageable, Long movieId, Long theatreId);
}
