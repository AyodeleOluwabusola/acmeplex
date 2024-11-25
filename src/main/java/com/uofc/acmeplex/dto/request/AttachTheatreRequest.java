package com.uofc.acmeplex.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class AttachTheatreRequest {

    @NotNull(message = "ShowTimes for Theatres is required")
    private Map<Long, List<LocalDateTime>> showTimes;

}