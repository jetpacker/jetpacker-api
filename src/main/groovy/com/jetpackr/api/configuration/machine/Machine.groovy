package com.jetpackr.api.configuration.machine

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import com.jetpackr.api.configuration.Metadata
import com.jetpackr.api.configuration.Parameter
import com.jetpackr.api.configuration.kit.Kit

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
class Machine extends Metadata {
    @Valid
    @NotNull
    Parameter box

    @Valid
    @NotNull
    Parameter memory

    @Valid
    @NotNull
    Parameter synchronization

    @Valid
    @NotNull
    Timezone timezone
}