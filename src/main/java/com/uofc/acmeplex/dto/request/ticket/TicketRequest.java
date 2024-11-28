package com.uofc.acmeplex.dto.request.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TicketRequest {

    @NotNull(message = "Showtime ID is required")
    private Long showtimeId;
    @NotBlank(message = "Email is required")
    private String email;
    @NotNull(message = "Select seat(s)")
    private List<Long> seatIds;
}
