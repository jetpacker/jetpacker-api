package com.jetpackr.api.configuration.kit

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Created by donny on 24/1/17.
 */
@CompileStatic
class Kits {
    @Valid
    @NotNull
    Kit jdk

    @Valid
    @NotNull
    Kit node

    @Valid
    @JsonIgnore
    @NotNull
    Kit ruby
}