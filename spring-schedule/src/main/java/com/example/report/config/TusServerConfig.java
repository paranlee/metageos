package com.example.report.config;

import me.desair.tus.server.TusFileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TusServerConfig {

    @Value("${tus.server.data.directory}")
    protected String tusDataPath;

    @Value("${tus.client.api.path}")
    protected String apiPath;

    @Value("#{servletContext.contextPath}")
    private String servletContextPath;

    @Bean
    public TusFileUploadService tusFileUploadService() {
        return new TusFileUploadService()
                .withStoragePath(tusDataPath)
                .withDownloadFeature()
                .withUploadURI(servletContextPath + apiPath)
                .withThreadLocalCache(true);
    }
}
