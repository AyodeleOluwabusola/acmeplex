package com.uofc.acmeplex.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Bean to hold request scoped data
 * Interceptors would use this bean to access request scoped data
 */
@Getter
@Setter
@RequestScope
@Component
public class RequestBean {

    private String sessionId;
    private String principal;
    private String authorization;
}
