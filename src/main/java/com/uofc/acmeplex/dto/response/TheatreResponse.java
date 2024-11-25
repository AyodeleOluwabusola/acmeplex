package com.uofc.acmeplex.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TheatreResponse {
    private Long id;
    private String name;
    private String location;
}