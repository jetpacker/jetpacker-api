package io.jetpacker.api.configuration

import groovy.transform.CompileStatic
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
@ConfigurationProperties(prefix = "jetpackerProperties")
class JetpackerProperties {
    Machine ubuntu

    Kit openjdk
    Kit node
    Kit guard

    List<Container> databaseEngines
    List<Container> queueEngines
    List<Container> searchEngines
}