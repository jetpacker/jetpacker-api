package jetpacker.api.configuration

import groovy.transform.CompileStatic

/**
 * Created by donny on 30/10/16.
 */
@CompileStatic
class Releases {
    String defaultVersion
    List<String> versions
    Property property
}