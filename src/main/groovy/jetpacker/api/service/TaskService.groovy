package jetpacker.api.service

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import jetpacker.api.configuration.Endpoint
import jetpacker.api.configuration.JetpackerProperties
import jetpacker.api.configuration.Kit
import jetpacker.api.configuration.Releases
import jetpacker.api.configuration.Tab
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
class TaskService {
    private final JetpackerProperties jetpackerProperties
    private final AsyncRestTemplate asyncRestTemplate
    private final CacheService cacheService

    @Autowired
    TaskService(JetpackerProperties jetpackerProperties, AsyncRestTemplate asyncRestTemplate, CacheService cacheService) {
        this.jetpackerProperties = jetpackerProperties
        this.asyncRestTemplate = asyncRestTemplate
        this.cacheService = cacheService
    }

    @Scheduled(fixedRate = 30000L)
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
                    releases: new Releases(
                            selectedVersion: ":${candidate}_version",
                            defaultVersion: versions[0],
                            versions: versions
                    ),
                    tab: new Tab(
                            index: 1,
                            label: 'SDKMAN!'
                    )
            )
        }
    }
}
