package io.jetpacker.api.configuration

import groovy.transform.CompileStatic

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
class Machine extends Platform {
    Box box
    Parameter memory
    Timezone timezone
}