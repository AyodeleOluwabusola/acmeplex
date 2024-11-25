package com.uofc.acmeplex.logic;

import com.uofc.acmeplex.dto.response.auth.TokenInfo;

import java.util.Map;

public interface ITokenService {

    //SOLID principle: Dependency Inversion (Classes depending on abstractions)
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
    TokenInfo getAuthToken(String email, Map<String, Object> claims);

}
