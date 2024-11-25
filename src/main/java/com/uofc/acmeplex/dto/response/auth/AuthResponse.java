package com.uofc.acmeplex.dto.response.auth;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthResponse {

    private String firstName;
    private String lastName;
    private String email;
    private TokenInfo authenticationToken;

}
