package com.uofc.acmeplex.repository;

import com.uofc.acmeplex.entities.ShowtimeSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowTimeSeatsRepository extends JpaRepository<ShowtimeSeat, Long> {

}
