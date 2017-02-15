package io.jetpacker.api.configuration

import groovy.transform.CompileStatic
import io.jetpacker.api.configuration.container.Container
import io.jetpacker.api.configuration.kit.Kits
import io.jetpacker.api.configuration.machine.Machine
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
@ConfigurationProperties(prefix = "jetpacker")
class JetpackerProperties {
    Machine machine
    Kits kits
    List<Container> containers
}