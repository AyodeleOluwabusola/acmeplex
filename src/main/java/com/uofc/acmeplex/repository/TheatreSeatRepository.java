package com.uofc.acmeplex.repository;

import com.uofc.acmeplex.dto.request.TheatreSeatStatusDTO;
import com.uofc.acmeplex.entities.TheatreSeat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TheatreSeatRepository extends JpaRepository<TheatreSeat, Long> {

    Page<TheatreSeat> findAllByTheatreId(Pageable pageable, Long theatreId);

    @Query(value = "SELECT ts.id, ts.seat_number as seatNumber, ts.seat_row as seatRow,  " +
            "CASE  WHEN ss.theatre_seat_fk IS NOT NULL THEN 'TAKEN' ELSE 'AVAILABLE' END AS seatStatus  " +
            "FROM theatre_seats ts   " +
            "LEFT JOIN showtime_seats ss ON ss.theatre_seat_fk = ts.id  " +
            "AND ss.showtime_fk = :showtimeId", nativeQuery = true)
    Page<TheatreSeatStatusDTO> fetchSeatDistributionForShowtime(Pageable pageable, @Param("showtimeId") Long showtimeId);

    @Query(value = "SELECT ss.theatre_seat_fk FROM showtime_seats ss WHERE ss.showtime_fk = :showtimeId AND ss.theatre_seat_fk IN :theatreSeatIds", nativeQuery = true)
    List<Long> findExistingTheatreSeatIds(@Param("showtimeId") Long showtimeId, @Param("theatreSeatIds") List<Long> theatreSeatIds);


    @Transactional
    @Modifying
    @Query(value = "delete from showtime_seats ss where ss.theatre_seat_fk in :seatIds", nativeQuery = true)
    int deleteShowTimSeatsBySeatIds(List<Long> seatIds);
}
