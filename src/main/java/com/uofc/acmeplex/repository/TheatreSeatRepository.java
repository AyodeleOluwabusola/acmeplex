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

    @Query(value = "SELECT ts.id, ts.seat_number as seatNumber, ts.seat_row as seatRow, " +
            "CASE WHEN sts.theatre_seat_fk IS NOT NULL THEN 'TAKEN' ELSE 'AVAILABLE' END AS seatStatus " +
            "FROM  theatre_seats ts " +
            "LEFT JOIN  showtime_seats sts ON ts.id = sts.theatre_seat_fk AND sts.showtime_fk = :showtimeId " +
            "WHERE ts.theatre_fk = :theatreId", nativeQuery = true)
    Page<TheatreSeatStatusDTO> fetchSeatDistributionForShowtime(Pageable pageable, @Param("theatreId") Long theatreId, @Param("showtimeId") Long showtimeId);

    @Query(value = "SELECT ts.seats_fk FROM ticket_seats ts " +
            "LEFT JOIN showtime_seats ss on ss.theatre_seat_fk = ts.seats_fk " +
            "WHERE ts.seats_fk IN (:theatreSeatIds) and ss.showtime_fk = :showtimeId", nativeQuery = true)
    List<Long> findExistingReservedTheatreSeats(@Param("theatreSeatIds") List<Long> theatreSeatIds, Long showtimeId);


    @Transactional
    @Modifying
    @Query(value = "delete from showtime_seats ss where ss.theatre_seat_fk in :seatIds", nativeQuery = true)
    int deleteShowTimSeatsBySeatIds(List<Long> seatIds);
}
