package io.jetpacker.api.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
class Software extends Platform {
    @JsonIgnore
    Repository repository = Repository.None

    Parameter install

    Version version
}