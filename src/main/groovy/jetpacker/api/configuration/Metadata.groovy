package jetpacker.api.configuration

import groovy.transform.CompileStatic

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
class Metadata {
    String name
    String label
    String description

    Releases releases
}
