package io.jetpacker.api.configuration.kit

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import io.jetpacker.api.configuration.Platform

/**
 * Created by donny on 25/10/16.
 */
@CompileStatic
class Kit extends Platform {
    List<Extension> extensions

    @JsonIgnore
    Dependency dependency
}