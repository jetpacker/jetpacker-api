package com.jetpackr.api.configuration

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.CompileStatic

import javax.validation.constraints.NotBlank

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
class Metadata {
    @NotBlank
    String name

    String alias

    @NotBlank
    String label

    String description
}
