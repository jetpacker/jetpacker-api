package jetpacker.api.configuration

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.AutoClone
import groovy.transform.CompileStatic

/**
 * Created by donny on 25/10/16.
 */
@AutoClone
@CompileStatic
class Kit extends Application {
    List<Kit> extensions

    @JsonIgnore
    Kit dependency
}
