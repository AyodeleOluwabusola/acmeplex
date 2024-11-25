package com.uofc.acmeplex.dto.response.auth;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TokenInfo {
    private String token;
    private String refreshToken;
    private int expireIn;
    private int refreshTokenExpiresIn;

}
