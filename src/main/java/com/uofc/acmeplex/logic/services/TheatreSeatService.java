package com.uofc.acmeplex.logic.services;

import com.uofc.acmeplex.dto.request.TheatreSeatInfo;
import com.uofc.acmeplex.dto.response.IResponse;
import com.uofc.acmeplex.dto.response.ResponseCodeEnum;
import com.uofc.acmeplex.dto.response.ResponseData;
import com.uofc.acmeplex.entities.Theatre;
import com.uofc.acmeplex.entities.TheatreSeat;
import com.uofc.acmeplex.exception.CustomException;
import com.uofc.acmeplex.logic.ITheatreSeatService;
import com.uofc.acmeplex.repository.TheatreRepository;
import com.uofc.acmeplex.repository.TheatreSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class TheatreSeatService implements ITheatreSeatService {

    private final TheatreRepository theatreRepository;
    private final TheatreSeatRepository theatreSeatRepository;


    @Override
    public IResponse fetchTheatresSeats(Pageable pageable, Long theatreId) {
        Page<TheatreSeat> theatreSeats = theatreSeatRepository.findAllByTheatreId(pageable, theatreId);
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS, TheatreSeatInfo.fromEntities(theatreSeats.getContent()));
    }

    public IResponse createSeatsForTheatre(Long theatreId, Map<String, Integer> rows) {
        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() -> new CustomException("Theatre not found", HttpStatus.NOT_FOUND));

        List<TheatreSeat> theatreSeats = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : rows.entrySet()) {
            String rowLabel = entry.getKey();
            int seatCount = entry.getValue();

            for (int seatNumber = 1; seatNumber <= seatCount; seatNumber++) {
                TheatreSeat seat = new TheatreSeat();
                seat.setSeatRow(rowLabel);
                seat.setSeatNumber(seatNumber);
                seat.setTheatre(theatre);
                theatreSeats.add(seat);
            }
        }
        return ResponseData.getInstance(ResponseCodeEnum.SUCCESS,
                TheatreSeatInfo.fromEntities(theatreSeatRepository.saveAll(theatreSeats)));
    }
}
