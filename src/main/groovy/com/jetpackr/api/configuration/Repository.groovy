package com.jetpackr.api.configuration

import groovy.transform.CompileStatic

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
class Repository {
    static enum Type {
        SDKMAN,
        NPMRegistry,
        GitHub,
        DockerHub,
        None
    }

    @NotNull
    Type type

    @NotBlank
    String url
}