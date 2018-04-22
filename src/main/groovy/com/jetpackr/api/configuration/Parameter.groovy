package com.jetpackr.api.configuration

import groovy.transform.CompileStatic

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * Created by donny on 2/11/2016.
 */
@CompileStatic
class Parameter {
    @NotBlank
    String name

    @NotBlank
    String label

    String value

    @Valid
    @Size(min = 1)
    List<Option> options
}