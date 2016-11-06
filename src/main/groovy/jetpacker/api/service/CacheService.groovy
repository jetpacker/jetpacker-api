package jetpacker.api.service

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import jetpacker.api.cache.Bucket
import jetpacker.api.cache.Jetpacker
import jetpacker.api.configuration.*
import org.springframework.beans.factory.annotation.Autowired
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
    private final Jetpacker jetpacker = new Jetpacker()

    private final JetpackerProperties jetpackerProperties
    private final AsyncRestTemplate asyncRestTemplate

    @Autowired
    CacheService(JetpackerProperties jetpackerProperties, AsyncRestTemplate asyncRestTemplate) {
        this.jetpackerProperties = jetpackerProperties
        this.asyncRestTemplate = asyncRestTemplate

        initCache()
    }

    Jetpacker items() {
        jetpacker
    }

    private void initCache() {
        try {
            jetpacker.clear()

            Box ubuntu = jetpackerProperties.ubuntu.clone()
            Kit openjdk = jetpackerProperties.openjdk.clone()
            Kit node = jetpackerProperties.node.clone()
            Kit guard = jetpackerProperties.guard.clone()

            jetpacker.boxes << new Bucket<Box>(ubuntu.label, ubuntu)

            jetpacker.kits << new Bucket<Kit>(openjdk.label, openjdk)
            jetpacker.kits << new Bucket<Kit>(Endpoint.SdkMan.name, getSdkManCandidates())
            jetpacker.kits << new Bucket<Kit>(node.label, node)
            jetpacker.kits << new Bucket<Kit>(guard.label, guard)

            jetpackerProperties.databases.each { Container container ->
                jetpacker.databases << new Bucket<Container>(container.label, container)
            }

            jetpackerProperties.messageQueues.each { Container container ->
                jetpacker.messageQueues << new Bucket<Container>(container.label, container)
            }

            jetpackerProperties.searchEngines.each { Container container ->
                jetpacker.searchEngines << new Bucket<Container>(container.label, container)
            }
        } catch (InterruptedException | ExecutionException e) {}
    }

    @Scheduled(fixedRate = 30000L)
    private void updateCache() {
        // TODO: Update only the release versions
    }

    private List<Kit> getSdkManCandidates() {
        ResponseEntity<String> rawCandidates = asyncRestTemplate.getForEntity(Endpoint.SdkMan.url, String.class).get()

        rawCandidates.body.split(",").collect { String candidate ->
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