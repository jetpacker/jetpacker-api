package io.jetpacker.api.web.command

/**
 * Created by donny on 30/11/16.
 */
class Container extends Platform {
    String command
    String volumesFrom
    Map<String, String> ports
    Map<String, String> env
}
