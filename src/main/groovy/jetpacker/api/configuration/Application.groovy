package jetpacker.api.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
class Application extends Metadata {
    String namespace
    
    @JsonIgnore
    Endpoint endpoint = Endpoint.None

    Property install
}