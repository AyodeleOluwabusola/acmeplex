package com.uofc.acmeplex.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class CommonLogic {

    private final PasswordEncoder passwordEncoder;

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public static Map<String, Object> getEmptyDataResponse() {
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("totalElements", 0);
        metaData.put("totalPages", 0);
        metaData.put("content", Collections.emptyList());
        return metaData;
    }

}
