package com.jetpackr.api.configuration.container

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import com.jetpackr.api.configuration.Parameter
import com.jetpackr.api.configuration.Platform

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

    Type type

    @JsonIgnore
    String command

    @JsonIgnore
    Map<String, String> volumes

    @JsonIgnore
    List<Port> ports

    @JsonIgnore
    Map<String, String> environment

    List<Parameter> parameters
}