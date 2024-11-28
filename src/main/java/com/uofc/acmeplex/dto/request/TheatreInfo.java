package com.uofc.acmeplex.dto.request;

import com.uofc.acmeplex.entities.Theatre;
import com.uofc.acmeplex.entities.TheatreSeat;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class TheatreInfo {

    private Long id;

    @NotBlank(message = "Theatre name is required")
    private String name;

    private Long movieId;

    @NotBlank(message = "Theatre location is required")
    private String location;

    private List<TheatreSeat> theatreSeats;

    public static TheatreInfo fromEntity(Theatre theatre) {
        return TheatreInfo.builder()
                .id(theatre.getId())
                .name(theatre.getName())
                .movieId(theatre.getMovie().getId())
                .location(theatre.getLocation())
                .build();
    }

    public static List<TheatreInfo> fromEntities(List<Theatre> theatres) {
        if (theatres == null || theatres.isEmpty()) {
            return List.of();
        }
        return theatres.stream()
                .map(TheatreInfo::fromEntity)
                .toList();
    }

    public static Theatre mapToEntity(TheatreInfo dto) {
        Theatre theatre = new Theatre();
        theatre.setName(dto.getName());
        theatre.setLocation(dto.getLocation());
        theatre.setSeats(dto.getTheatreSeats());
        return theatre;
    }
}
