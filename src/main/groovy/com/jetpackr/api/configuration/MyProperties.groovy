package com.jetpackr.api.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import com.jetpackr.api.configuration.container.Container

import com.jetpackr.api.configuration.kit.Kits
import com.jetpackr.api.configuration.machine.Machine
import com.jetpackr.api.configuration.template.Templates
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
@ConfigurationProperties(prefix = "jetpackr")
@Validated
class MyProperties {
    @NotNull
    Machine machine

    @NotNull
    Kits kits

    @Valid
    @NotNull
    @Size(min = 1)
    Map<String, Container> containers

    @JsonIgnore
    @NotNull
    Templates templates
}