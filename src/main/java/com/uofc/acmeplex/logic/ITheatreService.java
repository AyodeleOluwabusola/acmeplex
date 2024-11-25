package com.uofc.acmeplex.logic;

import com.uofc.acmeplex.dto.request.TheatreInfo;
import com.uofc.acmeplex.dto.response.IResponse;
import org.springframework.data.domain.Pageable;

public interface ITheatreService {

    //SOLID principle: Dependency Inversion (Classes depending on abstractions)
    IResponse createTheatre(TheatreInfo theatreInfo);
    IResponse fetchTheatres(Pageable pageable);
    IResponse fetchTheatresByMovie(Pageable pageable, Long movieId);
    IResponse fetchShowTimesForMovieAndTheatre(Pageable pageable, Long movieId, Long theatreId);
}
