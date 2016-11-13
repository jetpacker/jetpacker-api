package jetpacker.api.service

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import jetpacker.api.configuration.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.AsyncRestTemplate

import java.util.concurrent.ExecutionException

/**
 * Created by donny on 30/10/16.
 */
@CompileStatic
@Slf4j
@Service
class CacheService {
    private final JetpackerProperties jetpackerProperties
    private final AsyncRestTemplate asyncRestTemplate

    @Autowired
    CacheService(JetpackerProperties jetpackerProperties, AsyncRestTemplate asyncRestTemplate) {
        this.jetpackerProperties = jetpackerProperties
        this.asyncRestTemplate = asyncRestTemplate

        initCache()
    }

    JetpackerProperties load() {
        jetpackerProperties
    }

    private void initCache() {
        try {
            jetpackerProperties.openjdk.extensions = getSdkManCandidates()

            [ jetpackerProperties.node, jetpackerProperties.guard ].each { Kit kit ->
                updateReleases(kit)
            }

            jetpackerProperties.databases.each { Container container ->
                updateReleases(container)
            }

            jetpackerProperties.messageQueues.each { Container container ->
                updateReleases(container)
            }

            jetpackerProperties.searchEngines.each { Container container ->
                updateReleases(container)
            }


        } catch (InterruptedException|ExecutionException e) {}
    }

    @Scheduled(fixedRate = 30000L)
    private void updateCache() {
        // TODO: Update only the release versions
    }

    private void updateReleases(Application application, Boolean dependency = false) {
        if (application instanceof Kit) {
            Kit kit = (Kit) application

            if (kit.dependency)
                updateReleases(kit.dependency, true)
        }

        Endpoint endpoint = application.endpoint

        if (endpoint && endpoint != Endpoint.SdkMan) {
            String url = endpoint.url.replace("{name}", application.name)

            if (application.namespace)
                url = url.replace("{namespace}", application.namespace)

            List<Metadata> metadataList = null

            if (endpoint == Endpoint.GitHub) {
                ResponseEntity<List<Metadata>> response = asyncRestTemplate.exchange(url, HttpMethod.GET, null,
                                                            new ParameterizedTypeReference<List<Metadata>>() {}).get()
                metadataList = response.body
            }

            if (endpoint == Endpoint.DockerHub) {
                ResponseEntity<Docker> response = asyncRestTemplate.exchange(url, HttpMethod.GET, null,
                                                    new ParameterizedTypeReference<Docker>() {}).get()
                Docker docker = response.body
                metadataList = docker.results

                while (docker.next) {
                    response =  asyncRestTemplate.exchange(docker.next, HttpMethod.GET, null,
                                    new ParameterizedTypeReference<Docker>() {}).get()
                    docker = response.body
                    metadataList += docker.results
                }

            }

            if (metadataList) {
                List<String> versions = metadataList.collect { Metadata metadata ->
                    String pattern = "^v?[0-9]+([\\._][0-9]+)*(${application.suffix})?\$"

                    if (metadata.name =~ pattern)
                        metadata.name
                }

                versions.removeAll(Arrays.asList(null))

                if (!dependency)
                    application.releases.versions = versions

                application.releases.defaultVersion = versions[0]
            }
        }
    }

    private List<Kit> getSdkManCandidates() throws ExecutionException, InterruptedException {
        ResponseEntity<String> response = asyncRestTemplate.getForEntity(Endpoint.SdkMan.url, String.class).get()

        response.body.split(",").collect { String candidate ->
            ResponseEntity<String> rawVersions = asyncRestTemplate.getForEntity("${Endpoint.SdkMan.url}/${candidate}", String.class).get()
            List<String> versions = Arrays.asList(rawVersions.body.split(",")).reverse()
            new Kit(name: candidate,
                    label: candidate,
                    releases: new Releases(
                            defaultVersion: versions[0],
                            versions: versions,
                            property: new Property(
                                    name: "${candidate}_version",
                                    label: "Version"
                            )
                    )
            )
        }
    }
}