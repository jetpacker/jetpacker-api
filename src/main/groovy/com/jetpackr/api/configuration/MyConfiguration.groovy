package com.jetpackr.api.configuration

import groovy.transform.CompileStatic
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

/**
 * Created by donny on 30/10/16.
 */
@CompileStatic
@Configuration
@EnableConfigurationProperties(MyProperties.class)
class MyConfiguration {
    @Bean
    RestTemplate restTemplate() {
        new RestTemplate()
    }
}