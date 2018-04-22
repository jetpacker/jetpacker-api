package com.jetpackr.api.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic

import javax.validation.Valid

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
class Platform extends Metadata {
    @Valid
    @JsonIgnore
    Repository repository

    @Valid
    Parameter install

    @Valid
    Parameter version
}