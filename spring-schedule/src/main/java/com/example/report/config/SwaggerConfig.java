package com.example.report.config;

import static springfox.documentation.service.ApiInfo.DEFAULT;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${project.name}")
    private String projectName;

    @Bean
    public Docket docket() {
        Docket docket = new Docket(SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.example.report"))
            .paths(PathSelectors.ant("/api/**"))
            .build();

        return docket;
    }

    // Optional
    private ApiInfo apiInfo() {
        ApiInfoBuilder info = new ApiInfoBuilder();
        info.title(String.format("%s %s", projectName, DEFAULT.getTitle()));

        return info.build();
    }
}
