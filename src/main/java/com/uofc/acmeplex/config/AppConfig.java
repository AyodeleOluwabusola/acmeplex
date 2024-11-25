package com.uofc.acmeplex.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder (){
        return  new BCryptPasswordEncoder(10 , new SecureRandom());
    }
}