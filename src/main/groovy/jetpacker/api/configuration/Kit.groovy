package jetpacker.api.configuration

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * Created by donny on 25/10/16.
 */
@CompileStatic
class Kit extends Application {
    List<String> plugins
    Kit dependency
}
