package com.miraclesoft.scvp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The Class ScvpApplication.
 *
 * @author Narendar Geesidi
 */
@SpringBootApplication
@EnableSwagger2
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ScvpApplication extends SpringBootServletInitializer {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(ScvpApplication.class, args);
    }

    /**
     * Api.
     *
     * @return the docket
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.miraclesoft.scvp")).build();
    }
}
