package jetpacker.api.service

import groovy.transform.CompileStatic
import jetpacker.api.configuration.Endpoint
import jetpacker.api.configuration.JetpackerProperties
import jetpacker.api.configuration.Kit
import jetpacker.api.configuration.Releases
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.AsyncRestTemplate

import java.util.concurrent.ExecutionException

/**
 * Created by wolf on 30/10/16.
 */
@CompileStatic
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
        } catch (InterruptedException | ExecutionException e) {}
    }

    List<Kit> getSdkManCandidates() {
        ResponseEntity<String> candidates = asyncRestTemplate.getForEntity(Endpoint.SdkMan.url, String.class).get()

        candidates.body.split(",").collect { String candidate ->
            ResponseEntity<String> versions = asyncRestTemplate.getForEntity("${Endpoint.SdkMan.url}/${candidate}", String.class).get()
            new Kit(name: candidate, releases: new Releases())
        }
    }
}
