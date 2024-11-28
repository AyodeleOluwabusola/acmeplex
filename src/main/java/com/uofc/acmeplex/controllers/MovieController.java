package com.uofc.acmeplex.controllers;

import com.uofc.acmeplex.dto.request.AttachTheatreRequest;
import com.uofc.acmeplex.dto.request.movie.MovieRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.logic.IMovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("movie")
public class MovieController {

    private final IMovieService movieService;

    @PostMapping
    public IResponse createMovie(@RequestBody @Valid MovieRequest movieRequest) {
        return movieService.createMovie(movieRequest);
    }

    @GetMapping("fetch")
    public IResponse fetchMovies(Pageable pageable, @RequestParam(name = "name", required = false) String name) {
        return movieService.retrieveMovies(pageable, name);
    }

    @PostMapping("/{movieId}/attach-theatres")
    public IResponse attachMovieToTheatres(@PathVariable Long movieId,
                                                        @RequestBody @Valid AttachTheatreRequest request) {
        return movieService.attachMovieToTheatres(movieId, request.getShowTimes());
    }

}
