package jetpacker.api.configuration

import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.util.logging.Slf4j

/**
 * Created by donny on 30/10/16.
 */
@CompileStatic
class Container extends Application {
    String command
    List<Volume> volumes
    List<Port> publishedPorts
    Map<String, String> environmentVariables
}