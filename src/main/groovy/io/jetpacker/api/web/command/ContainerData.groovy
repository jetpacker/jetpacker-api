package io.jetpacker.api.web.command

/**
 * Created by donny on 30/11/16.
 */
class ContainerData {
    String command
    Map<String, String> volumes
    Map<String, String> publishedPorts
    Map<String, String> environmentVariables
}
