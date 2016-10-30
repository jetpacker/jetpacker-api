package jetpacker.api.configuration

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * Created by donny on 25/10/16.
 */
@ToString(includeNames = true, includeSuper = true)
@CompileStatic
class Kit extends Application {
    List<String> plugins
    Kit dependency
}
