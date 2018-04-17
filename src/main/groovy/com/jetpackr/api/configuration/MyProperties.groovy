package com.jetpackr.api.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import com.jetpackr.api.configuration.container.Container

import com.jetpackr.api.configuration.kit.Kits
import com.jetpackr.api.configuration.machine.Machine
import com.jetpackr.api.configuration.template.Templates
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
@ConfigurationProperties(prefix = "jetpackr")
@Validated
class MyProperties {
    Machine machine
    Kits kits
    Map<String, Container> containers

    @JsonIgnore
    Templates templates
}