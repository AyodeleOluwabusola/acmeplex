package com.uofc.acmeplex.repository;

import com.uofc.acmeplex.entities.TheatreSeat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheatreSeatRepository extends JpaRepository<TheatreSeat, Long> {

    Page<TheatreSeat> findAllByTheatreId(Pageable pageable, Long theatreId);
}
