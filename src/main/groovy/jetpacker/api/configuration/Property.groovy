package jetpacker.api.configuration

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * Created by donny on 2/11/2016.
 */
@ToString(includeNames = true)
@CompileStatic
class Property {
    String name
    String label
    String value
}