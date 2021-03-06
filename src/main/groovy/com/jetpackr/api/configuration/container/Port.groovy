package com.jetpackr.api.configuration.container

import groovy.transform.CompileStatic
import groovy.transform.ToString

import javax.validation.constraints.NotBlank

/**
 * Created by donny on 2/11/2016.
 */
@CompileStatic
class Port {
    @NotBlank
    String host

    @NotBlank
    String container
}