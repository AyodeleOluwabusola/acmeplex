package com.uofc.acmeplex.controllers;

import com.uofc.acmeplex.dto.request.TheatreInfo;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.logic.ITheatreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("theatre")
public class TheatreController {

    private final ITheatreService theatreService;

    @PostMapping
    public IResponse createTheatre(@RequestBody @Valid TheatreInfo theatreInfo) {
        return theatreService.createTheatre(theatreInfo);
    }

    @GetMapping("fetch-all")
    public IResponse fetchTheatres(Pageable pageable) {
        return theatreService.fetchTheatres(pageable);
    }

    @GetMapping("fetch")
    public IResponse fetchTheatresForMovie(Pageable pageable, @RequestParam Long movieId) {
        return theatreService.fetchTheatresByMovie(pageable, movieId);
    }

    @GetMapping("show-times")
    public IResponse fetchShowTimesForMovieAndTheatre(Pageable pageable, @RequestParam Long movieId, @RequestParam Long theatreId) {
        return theatreService.fetchShowTimesForMovieAndTheatre(pageable, movieId, theatreId);
    }
}
