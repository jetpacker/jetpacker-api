package io.jetpacker.api.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic

/**
 * Created by donny on 25/10/16.
 */
@CompileStatic
class Kit extends Software {
    String alias

    List<Kit> extensions

    @JsonIgnore
    Kit dependency
}