package io.jetpacker.api.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import io.jetpacker.api.configuration.container.Container
import io.jetpacker.api.configuration.container.DataContainer
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
    Map<String, Container> containers

    @JsonIgnore
    DataContainer dataContainer
}