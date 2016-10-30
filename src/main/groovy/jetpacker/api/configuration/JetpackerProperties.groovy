package jetpacker.api.configuration

import groovy.transform.CompileStatic
import groovy.transform.ToString
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Created by donny on 24/10/2016.
 */
@ToString(includeNames = true)
@ConfigurationProperties(prefix = "jetpacker")
@CompileStatic
class JetpackerProperties {
    List<Box> boxes

    List<Integer> openjdks
    List<Kit> kits

    List<Container> databases
    List<Container> messageQueues
    List<Container> searchEngines
}