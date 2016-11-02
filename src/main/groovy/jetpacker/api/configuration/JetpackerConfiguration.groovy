package jetpacker.api.configuration

import groovy.text.StreamingTemplateEngine
import groovy.text.TemplateEngine
import groovy.transform.CompileStatic
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.AsyncRestTemplate

/**
 * Created by donny on 30/10/16.
 */
@Configuration
@EnableConfigurationProperties(JetpackerProperties.class)
@CompileStatic
class JetpackerConfiguration {
    @Bean
    AsyncRestTemplate asyncRestTemplate() {
        new AsyncRestTemplate()
    }

    @Bean
    TemplateEngine templateEngine() {
        new StreamingTemplateEngine()
    }
}