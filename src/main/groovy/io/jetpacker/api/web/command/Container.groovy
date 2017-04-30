package io.jetpacker.api.web.command

/**
 * Created by donny on 30/11/16.
 */
class Container {
    String version
    String command
    Map<String, String> volumes
    Map<String, String> ports
    Map<String, String> env
}
