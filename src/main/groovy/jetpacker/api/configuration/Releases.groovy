package jetpacker.api.configuration

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * Created by donny on 30/10/16.
 */
@ToString(includeNames = true)
@CompileStatic
class Releases {
    String defaultVersion
    List<String> versions
}