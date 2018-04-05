package io.jetpacker.api.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
class Platform extends Metadata {
    @JsonIgnore
    Repository repository
    Parameter install
    Parameter version
}