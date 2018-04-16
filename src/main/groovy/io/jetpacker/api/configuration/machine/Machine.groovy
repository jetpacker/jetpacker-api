package io.jetpacker.api.configuration.machine

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import io.jetpacker.api.configuration.Metadata
import io.jetpacker.api.configuration.Parameter
import io.jetpacker.api.configuration.kit.Kit

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