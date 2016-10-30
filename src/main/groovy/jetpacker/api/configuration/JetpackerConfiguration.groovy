package jetpacker.api.configuration

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Created by wolf on 30/10/16.
 */
@Configuration
@EnableConfigurationProperties(JetpackerProperties.class)
class JetpackerConfiguration {
}
