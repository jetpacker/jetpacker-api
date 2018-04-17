package com.jetpackr.api.configuration.kit

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import com.jetpackr.api.configuration.Platform

/**
 * Created by donny on 25/10/16.
 */
@CompileStatic
class Kit extends Platform {
    List<Extension> extensions // Extension[] extensions. Using array so can be passed as elipsis?

    @JsonIgnore
    Dependency dependency
}