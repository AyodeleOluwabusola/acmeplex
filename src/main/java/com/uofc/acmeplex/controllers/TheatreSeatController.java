package com.uofc.acmeplex.controllers;

import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.logic.ITheatreSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("theatre-seat")
public class TheatreSeatController {

    private final ITheatreSeatService theatreSeatService;

    @PostMapping("/{theatreId}/create-seats")
    public IResponse createSeatsForTheatre(@PathVariable Long theatreId, @RequestBody Map<String, Integer> seatDistribution) {
        return theatreSeatService.createSeatsForTheatre(theatreId, seatDistribution);
    }

    @GetMapping("fetch")
    public IResponse fetchSeatsForTheatre(Pageable pageable, @RequestParam Long theatreId) {
        return theatreSeatService.fetchTheatresSeats(pageable, theatreId);
    }

    @GetMapping("seat-distribution")
    public IResponse fetchSeatDistribution(Pageable pageable, @RequestParam Long showtimeId) {
        return theatreSeatService.fetchSeatDistribution(pageable, showtimeId);
    }
}
