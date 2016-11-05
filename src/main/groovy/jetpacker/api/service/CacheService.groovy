package jetpacker.api.service

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
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
    private final JetpackerProperties jetpackerProperties
    private final AsyncRestTemplate asyncRestTemplate

    @Autowired
    CacheService(JetpackerProperties jetpackerProperties, AsyncRestTemplate asyncRestTemplate) {
        this.jetpackerProperties = jetpackerProperties
        this.asyncRestTemplate = asyncRestTemplate
    }

    @Scheduled(fixedRate = 360000L)
    void updateCache() {
        try {
            getSdkManCandidates()
            // TODO: clone the jetpacker properties items and assigned to cache.
        } catch (InterruptedException | ExecutionException e) {}
    }

    List<Kit> getSdkManCandidates() {
        ResponseEntity<String> rawCandidates = asyncRestTemplate.getForEntity(Endpoint.SdkMan.url, String.class).get()

        List<Kit> kits = rawCandidates.body.split(",").collect { String candidate ->
            ResponseEntity<String> rawVersions = asyncRestTemplate.getForEntity("${Endpoint.SdkMan.url}/${candidate}", String.class).get()
            List<String> versions = Arrays.asList(rawVersions.body.split(",")).reverse()
            new Kit(name: candidate,
                    label: "SDKMAN!",
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
