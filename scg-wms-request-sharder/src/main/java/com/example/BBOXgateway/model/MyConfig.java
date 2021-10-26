package com.example.BBOXgateway.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "route")
@Configuration
@Setter
@Getter
public class MyConfig {
    private List<RouteElm> list;

    @Setter
    @Getter
    public static class RouteElm {
        private String uri;
        private int minx, miny, maxx, maxy;
    }
}

