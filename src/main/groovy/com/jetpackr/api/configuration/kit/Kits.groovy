package com.jetpackr.api.configuration.kit

import groovy.transform.CompileStatic

import javax.validation.Valid

/**
 * Created by donny on 24/1/17.
 */
@CompileStatic
class Kits {
    @Valid
    Kit jdk

    @Valid
    Kit node
}