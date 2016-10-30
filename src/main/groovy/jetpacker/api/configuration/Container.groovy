package jetpacker.api.configuration

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * Created by donny on 30/10/16.
 */
@ToString(includeNames = true, includeSuper = true)
@CompileStatic
class Container extends Application {
    Map<String, String> properties
}