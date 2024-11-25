package com.uofc.acmeplex.security;

import com.uofc.acmeplex.logic.services.JsonWebTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * Filter to authenticate the user based on the token
 * If the token is valid (not expired), the user is authenticated
 * Collect some headers from the request and store them in the RequestBean
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebAuthenticationFilter extends OncePerRequestFilter {

    private final JsonWebTokenService jsonWebTokenService;
    private final RequestBean requestBean;

    /**
     * Filter to authenticate the user based on the token
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            if (jsonWebTokenService.isTokenExpired(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid or expired");
                return;
            }

            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("DEFAULT_USER");

            UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
                    requestBean.getPrincipal(), null, Collections.singletonList(simpleGrantedAuthority));
            userToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(userToken);

            requestBean.setSessionId(request.getHeader("x-session-id"));
            populateRequestWithHeaders(token);
        }

        filterChain.doFilter(request, response);
    }


    /**
     * Populate the request bean with headers
     *
     * @param token token
     */
    private void populateRequestWithHeaders(String token) {
        Optional<Claims> claimsOptional = jsonWebTokenService.getClaim(token);
        claimsOptional.ifPresent(claims -> {
            String username = claims.get("email", String.class);
            log.debug("Setting username to requestBean: {}", username);
            requestBean.setPrincipal(username);
        });
    }
}
