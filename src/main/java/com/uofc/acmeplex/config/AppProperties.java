package com.uofc.acmeplex.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties
public class AppProperties {

    @Autowired
    private Environment env;

    private String movieAnnouncedMessage = """
            We are pleased to inform you that a thrilling new movie has just hit the screens at your favorite theatre! Our team is delighted to share this exciting update with you..

            Rest assured, we will keep you updated on upcoming releases and special screenings to ensure you never miss out on cinematic experiences. If you have any preferences or would like to share feedback, please feel free to reach us at support@acmeplex.com.                                                                                                                                                                                                                                                                                                                                                                                                                                                                           .

            Thank you for choosing Acmeplex!""";
}
