package com.uofc.acmeplex.logic;

import com.uofc.acmeplex.dto.request.movie.MovieRequest;
import com.uofc.acmeplex.dto.response.IResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IMovieService {

    //SOLID principle: Dependency Inversion (Classes depending on abstractions)
    IResponse createMovie(MovieRequest movieRequest);
    IResponse retrieveMovies(Pageable pageable);

    IResponse attachMovieToTheatres(Long movieId, Map<Long, List<LocalDateTime>> showTimes);
}
