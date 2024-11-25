package com.uofc.acmeplex.dto.request.ticket;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TicketRequest {

    @NotNull(message = "Showtime ID is required")
    private Long showtimeId;
    @NotNull(message = "Theatre ID is required")
    private Long theatreId;
    @NotNull(message = "Select seat(s)")
    private List<Long> seatIds;
}
