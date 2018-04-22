package com.jetpackr.api.configuration

import groovy.transform.CompileStatic

import javax.validation.constraints.NotBlank

/**
 * Created by wolf on 26/2/17.
 */
@CompileStatic
class Option {
    String label

    @NotBlank
    String value
}