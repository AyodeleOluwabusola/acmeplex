package com.uofc.acmeplex.logic;

import com.uofc.acmeplex.dto.response.IResponse;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ITheatreSeatService {

    //SOLID principle: Dependency Inversion (Classes depending on abstractions)
    IResponse createSeatsForTheatre(Long theatreId, Map<String, Integer> rows);
    IResponse fetchTheatresSeats(Pageable pageable, Long theatreId);
    IResponse fetchSeatDistribution(Pageable pageable, Long showtimeId);
}
