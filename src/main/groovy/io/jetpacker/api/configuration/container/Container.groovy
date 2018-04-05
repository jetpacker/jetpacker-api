package io.jetpacker.api.configuration.container

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import io.jetpacker.api.configuration.Parameter
import io.jetpacker.api.configuration.Platform

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
    List<Volume> volumes

    @JsonIgnore
    List<Port> ports

    @JsonIgnore
    Map<String, String> env

    List<Parameter> parameters
}