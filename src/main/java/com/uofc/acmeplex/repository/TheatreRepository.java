package com.uofc.acmeplex.repository;

import com.uofc.acmeplex.entities.Theatre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {
    Page<Theatre> findAllByMovieId(Pageable pageable, Long movieId);
    boolean existsByName(String name);
}
