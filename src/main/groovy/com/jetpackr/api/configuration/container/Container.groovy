package com.jetpackr.api.configuration.container

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import com.jetpackr.api.configuration.Parameter
import com.jetpackr.api.configuration.Platform

import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

/**
 * Created by donny on 30/10/16.
 */
@CompileStatic
class Container extends Platform {
    static enum Type {
        DataStore,
        MessageBroker,
        SearchEngine
    }

    @NotNull
    Type type

    @JsonIgnore
    String command

    @JsonIgnore
    @Size(min = 1)
    Map<String, String> volumes

    @JsonIgnore
    @Valid
    @Size(min = 1)
    List<Port> ports

    @JsonIgnore
    @Size(min = 1)
    Map<String, String> environment

    @Size(min = 1)
    List<Parameter> parameters
}