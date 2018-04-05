package io.jetpacker.api.configuration

import groovy.transform.CompileStatic

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
class Repository {
    static enum Type {
        GitHub,
        DockerHub,
        None
    }

    Type type
    String url
}