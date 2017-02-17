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
    Type type

    @JsonIgnore
    String command

    @JsonIgnore
    List<Volume> volumes

    @JsonIgnore
    List<Port> publishedPorts

    @JsonIgnore
    Map<String, String> environmentVariables

    List<Parameter> parameters
}