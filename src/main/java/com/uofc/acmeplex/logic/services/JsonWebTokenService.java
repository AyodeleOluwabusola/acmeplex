package com.uofc.acmeplex.logic.services;

import com.uofc.acmeplex.dto.response.auth.TokenInfo;
import com.uofc.acmeplex.logic.ITokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Service class for JsonWebToken
 * Facilitates the JSON Tokenization us functionality
 * Fulfils SOLID principle: Single Responsibility- Only manages JSON Tokenization functionalities like validity, expiration etc
 */

@Slf4j
@Service
public class JsonWebTokenService implements ITokenService {


    @Value("${jwt.access-token.duration}")
    public int jwtTokenExpirationDuration;

    @Value("${jwt.refresh-token.duration}")
    public int jwtRefreshTokenExpirationDuration;

    @Value("${jwt.secret}")
    private String secret;

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public boolean validateToken(String token) {
        return isTokenExpired(token);
    }

    @Override
    public TokenInfo getAuthToken(String email, Map<String, Object> claims) {

        Map<String, Object> refreshTokenClaim = new HashMap<>();
        refreshTokenClaim.put("refreshToken", "true");

        return TokenInfo.builder()
                .token(doGenerateToken(claims, email, jwtTokenExpirationDuration))
                .refreshToken(doGenerateToken(refreshTokenClaim, email, jwtRefreshTokenExpirationDuration))
                .expireIn(jwtTokenExpirationDuration)
                .refreshTokenExpiresIn(jwtRefreshTokenExpirationDuration)
                .build();

    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieving any information from token we will need the secret key
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    //check if the token has expired
    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.debug("Expired token ", e);
        }
        return Boolean.TRUE;
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    public String doGenerateToken(Map<String, Object> claims, String subject, int timeOut) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + timeOut))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public Optional<Claims> getClaim (String token ){
        return Optional.ofNullable(getAllClaimsFromToken(token));
    }
}
