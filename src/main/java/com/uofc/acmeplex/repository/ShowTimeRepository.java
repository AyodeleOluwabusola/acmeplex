package com.uofc.acmeplex.repository;

import com.uofc.acmeplex.entities.Showtime;
import com.uofc.acmeplex.entities.Theatre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowTimeRepository extends JpaRepository<Showtime, Long> {

    Page<Showtime> findAllByMovieIdAndTheatreId(Pageable pageable, Long movieId, Long theatreId);

    @Query("select t from Theatre t JOIN Showtime st ON st.theatre.id = t.id where st.movie.id = :movieId group by t.id")
    Page<Theatre> findAllTheatresByMovie(Pageable pageable, Long movieId);
}
