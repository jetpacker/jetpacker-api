package jetpacker.api.configuration

import groovy.transform.CompileStatic

/**
 * Created by donny on 24/10/2016.
 */
@CompileStatic
enum Repository {
    SdkMan("http://api.sdkman.io/candidates"),
    GitHub("https://api.github.com/repos/{namespace}/{name}/tags"),
    DockerHub("https://registry.hub.docker.com/v2/repositories/library/{name}/tags"),
    None("http://127.0.0.1")

    String url

    Repository(String url) {
        this.url = url
    }
}