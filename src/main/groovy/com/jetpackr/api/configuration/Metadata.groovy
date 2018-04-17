package com.jetpackr.api.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.CompileStatic

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
class Metadata {
    String name

    @JsonIgnore
    String alias

    String label
    String description
}