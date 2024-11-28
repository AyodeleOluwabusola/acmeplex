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
            We are pleased to inform you that we have successfully received your request for a corporate filing. Our team is now ready to begin processing it.

            Rest assured, we will keep you updated on the progress and notify you once the report is ready for download. If you have any additional information or special instructions, please feel free to reach us at support@ctbycoronation.com.

            Thank you for choosing Captable by Coronation!""";
}
