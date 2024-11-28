package com.uofc.acmeplex.config;


import freemarker.cache.FileTemplateLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.io.File;
import java.io.IOException;

@Configuration
public class EmailConfig {

    @Value("${template.path}")
    private String templatePath;
    @Bean
    public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration()  throws IOException{
        FreeMarkerConfigurationFactoryBean fmConfigFactoryBean = new FreeMarkerConfigurationFactoryBean();

        // Set up the template loader with the file system directory
        FileTemplateLoader templateLoader = new FileTemplateLoader(new File(templatePath));
        fmConfigFactoryBean.setTemplateLoaderPaths(templatePath);
        fmConfigFactoryBean.setPreTemplateLoaders(templateLoader);


        return fmConfigFactoryBean;
    }




}
