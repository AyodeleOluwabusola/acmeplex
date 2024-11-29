package com.uofc.acmeplex.logic.services;

import com.uofc.acmeplex.dto.request.ShowTimeInfo;
import com.uofc.acmeplex.dto.request.TheatreInfo;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.dto.response.ResponseCodeEnum;
import com.uofc.acmeplex.dto.response.ResponseData;
import com.uofc.acmeplex.entities.Showtime;
import com.uofc.acmeplex.entities.Theatre;
import com.uofc.acmeplex.exception.CustomException;
import com.uofc.acmeplex.logic.ITheatreService;
import com.uofc.acmeplex.repository.MovieRepository;
import com.uofc.acmeplex.repository.ShowTimeRepository;
import com.uofc.acmeplex.repository.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TheatreService implements ITheatreService {

    private final TheatreRepository theatreRepository;
    private final ShowTimeRepository showTimeRepository;
    private final MovieRepository movieRepository;

    @Override
    public IResponse createTheatre(TheatreInfo theatreInfo) {
        if (theatreRepository.existsByName(theatreInfo.getName())) {
            throw new CustomException("Theatre already exist", HttpStatus.CONFLICT);
        }
        Theatre theatre = TheatreInfo.mapToEntity(theatreInfo);
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, theatreRepository.save(theatre));
    }

    @Override
    public IResponse fetchTheatres(Pageable pageable) {
        Page<Theatre> theatres = theatreRepository.findAll(pageable);
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, TheatreInfo.fromEntities(theatres.getContent()));
    }

    @Override
    public IResponse fetchTheatresByMovie(Pageable pageable, Long movieId) {
        Page<Theatre> theatres = theatreRepository.findAllTheatresByMovie(pageable, movieId);
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, TheatreInfo.fromEntities(theatres.getContent()));
    }


    @Override
    public IResponse fetchShowTimesForMovieAndTheatre(Pageable pageable, Long movieId, Long theatreId) {
        Page<Showtime> showtimes = showTimeRepository.findAllByMovieIdAndTheatreId(pageable, movieId, theatreId);
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, ShowTimeInfo.fromEntities(showtimes.getContent()));
    }
}
