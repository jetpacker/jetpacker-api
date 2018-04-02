package io.jetpacker.api.configuration

import groovy.transform.CompileStatic
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.AsyncRestTemplate

/**
 * Created by donny on 30/10/16.
 */
@CompileStatic
@Configuration
@EnableConfigurationProperties(JetpackerProperties.class)
class JetpackerConfiguration {
    @Bean
    AsyncRestTemplate asyncRestTemplate() {
        new AsyncRestTemplate()
    }
}