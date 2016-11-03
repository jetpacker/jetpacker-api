package jetpacker.api.configuration

import groovy.transform.CompileStatic
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
@ConfigurationProperties(prefix = "jetpacker")
class JetpackerProperties {
    List<Box> boxes
    List<Kit> kits
    List<Container> databases
    List<Container> messageQueues
    List<Container> searchEngines
}