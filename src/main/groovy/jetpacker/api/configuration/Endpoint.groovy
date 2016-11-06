package jetpacker.api.configuration

import groovy.transform.CompileStatic

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
enum Endpoint {
    SdkMan("SDKMAN!", "http://api.sdkman.io/candidates"),
    GitHub("GitHub", "https://api.github.com/repos/{namespace}/{name}/tags"),
    DockerHub("DockerHub", "https://registry.hub.docker.com/v2/repositories/library/{name}/tags"),
    None("None", "http://127.0.0.1")

    String name
    String url

    Endpoint(String name, String url) {
        this.name = name
        this.url = url
    }
}