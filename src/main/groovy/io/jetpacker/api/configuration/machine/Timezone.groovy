package io.jetpacker.api.configuration.machine

import groovy.transform.CompileStatic
import io.jetpacker.api.configuration.Parameter

/**
 * Created by donny on 15/11/16.
 */
@CompileStatic
class Timezone extends Parameter {
    List<String> ids
}