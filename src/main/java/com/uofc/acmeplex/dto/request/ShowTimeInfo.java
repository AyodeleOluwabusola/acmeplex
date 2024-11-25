package com.uofc.acmeplex.dto.request;

import com.uofc.acmeplex.entities.Showtime;
import com.uofc.acmeplex.entities.TheatreSeat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class ShowTimeInfo {

    private Long id;

    private LocalDateTime startTime;

    private List<TheatreSeat> theatreSeats;

    public static ShowTimeInfo fromEntity(Showtime showtime) {
        return ShowTimeInfo.builder()
                .id(showtime.getId())
                .startTime(showtime.getStartTime())
                .build();
    }

    public static List<ShowTimeInfo> fromEntities(List<Showtime> showtimes) {
        if (showtimes == null || showtimes.isEmpty()) {
            return List.of();
        }
        return showtimes.stream()
                .map(ShowTimeInfo::fromEntity)
                .toList();
    }

}
