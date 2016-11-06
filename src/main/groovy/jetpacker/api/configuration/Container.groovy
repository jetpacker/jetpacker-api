package jetpacker.api.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.AutoClone
import groovy.transform.CompileStatic

/**
 * Created by donny on 30/10/16.
 */
@AutoClone
@CompileStatic
class Container extends Application {
    @JsonIgnore
    String command

    @JsonIgnore
    List<Volume> volumes

    @JsonIgnore
    List<Port> publishedPorts

    @JsonIgnore
    Map<String, String> environmentVariables

    List<Property> properties
}