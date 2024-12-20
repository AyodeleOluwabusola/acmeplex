package com.uofc.acmeplex.repository;

import com.uofc.acmeplex.entities.Showtime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowTimeRepository extends JpaRepository<Showtime, Long> {

    Page<Showtime> findAllByMovieIdAndTheatreId(Pageable pageable, Long movieId, Long theatreId);
}
