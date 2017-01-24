package io.jetpacker.api.configuration

import groovy.transform.CompileStatic
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
@ConfigurationProperties(prefix = "jetpacker")
class JetpackerProperties {
    VirtualMachines virtualMachines
    DevelopmentKits developmentKits

    List<Container> databaseServers
    List<Container> messageBrokers
    List<Container> searchEngines
}