package io.jetpacker.api.configuration.machine

import groovy.transform.CompileStatic
import io.jetpacker.api.configuration.Parameter
import io.jetpacker.api.configuration.Metadata

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
class Machine extends Metadata {
    Box box
    Parameter memory
    Timezone timezone
}