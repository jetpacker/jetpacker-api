package io.jetpacker.api.configuration.machine

import groovy.transform.CompileStatic
import io.jetpacker.api.configuration.Parameter

/**
 * Created by donny on 14/2/17.
 */
@CompileStatic
class Box extends Parameter {
    Map<String, String> releases
}