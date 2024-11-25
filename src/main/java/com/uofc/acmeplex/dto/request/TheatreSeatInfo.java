package com.uofc.acmeplex.dto.request;

import com.uofc.acmeplex.entities.TheatreSeat;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class TheatreSeatInfo {

    private Long id;

    private String seat;

    private String seatRow;

    private String  seatNumber;

    private boolean seatTaken;

    public static TheatreSeatInfo fromEntity(TheatreSeat theatreSeat) {
        return TheatreSeatInfo.builder()
                .id(theatreSeat.getId())
                .seatRow(theatreSeat.getSeatRow())
                .seatNumber(String.valueOf(theatreSeat.getSeatNumber()))
                .seat(theatreSeat.getSeatRow() + theatreSeat.getSeatNumber())
                .seatTaken(theatreSeat.isTaken())
                .build();
    }

    public static List<TheatreSeatInfo> fromEntities(List<TheatreSeat> theatreSeats) {
        if(theatreSeats == null || theatreSeats.isEmpty()){
            return List.of();
        }
        return theatreSeats.stream()
                .map(TheatreSeatInfo::fromEntity)
                .toList();
    }
}
