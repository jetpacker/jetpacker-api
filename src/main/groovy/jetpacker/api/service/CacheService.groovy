package jetpacker.api.service

import com.github.zafarkhaja.semver.Version
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

import javax.xml.ws.Response
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
                if (kit.endpoint != null && kit.endpoint == Endpoint.GitHub) {
                    String url = Endpoint.GitHub.url.replace("{namespace}", kit.namespace)
                                                    .replace("{name}", kit.name)

                    ResponseEntity<List<Metadata>> response = asyncRestTemplate.exchange(url, HttpMethod.GET, null,
                                                                new ParameterizedTypeReference<List<Metadata>>() {}).get()

                    kit.releases.versions = response.body.collect { Metadata metadata -> metadata.name }
                    kit.releases.defaultVersion = kit.releases.versions[0]
                }


                Kit dependency = kit.dependency

                if (dependency && dependency.endpoint != null && dependency.endpoint == Endpoint.GitHub) {
                    String url = Endpoint.GitHub.url.replace("{namespace}", dependency.namespace)
                                                    .replace("{name}", dependency.name)

                    ResponseEntity<List<Metadata>> response = asyncRestTemplate.exchange(url, HttpMethod.GET, null,
                            new ParameterizedTypeReference<List<Metadata>>() {}).get()

                    List<String> versions = response.body.collect { Metadata metadata ->
                        if (metadata.name =~ /^v?[0-9]+(\.|_)[0-9]+(\.|_)[0-9]+$/)
                            metadata.name
                    }

                    versions.removeAll(Arrays.asList(null))
                    dependency.releases.defaultVersion = versions[0]
                }
            }

        } catch (InterruptedException | ExecutionException e) {}
    }

    @Scheduled(fixedRate = 30000L)
    private void updateCache() {
        // TODO: Update only the release versions
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