package io.jetpacker.api.configuration

import groovy.transform.CompileStatic

/**
 * Created by donny on 2/11/2016.
 */
@CompileStatic
class Parameter {
    String name
    String label
    String value

    List<Option> options
}