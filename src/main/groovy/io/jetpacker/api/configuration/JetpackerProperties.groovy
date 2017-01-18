package io.jetpacker.api.configuration

import groovy.transform.CompileStatic
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
@ConfigurationProperties(prefix = "jetpacker")
class JetpackerProperties {
    Machine ubuntu

    Kit openjdk
    Kit node
    Kit guard

    List<Container> databaseServers
    List<Container> messageBrokers
    List<Container> searchEngines
}