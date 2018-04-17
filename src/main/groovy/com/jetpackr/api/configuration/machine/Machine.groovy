package com.jetpackr.api.configuration.machine

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import com.jetpackr.api.configuration.Metadata
import com.jetpackr.api.configuration.Parameter
import com.jetpackr.api.configuration.kit.Kit

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
class Machine extends Metadata {
    Parameter box
    Parameter memory
    Parameter synchronization
    Timezone timezone

    @JsonIgnore
    Kit guard
}