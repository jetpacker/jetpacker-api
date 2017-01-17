package io.jetpacker.api.core

import groovy.util.logging.Slf4j
import io.jetpacker.api.configuration.Container
import io.jetpacker.api.configuration.JetpackerProperties
import io.jetpacker.api.configuration.Kit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import java.util.concurrent.ExecutionException

/**
 * Created by donny on 22/11/16.
 */
@Slf4j
@Service
class GeneratorService {
    private final JetpackerProperties jetpackerProperties
    private final RepositoryService repositoryService

    @Autowired
    GeneratorService(RepositoryService repositoryService,
                     JetpackerProperties jetpackerProperties) {
        this.repositoryService = repositoryService
        this.jetpackerProperties = jetpackerProperties
    }

    @PostConstruct
    void setUp() {
        try {
            // TODO: TimeZone can be refactored for better testability
            log.info "Loading timezones"
            jetpackerProperties.ubuntu.timezone.availableIds = TimeZone.availableIDs.collect { String id ->
                if (!id.startsWith("SystemV"))
                    return id
            }

            jetpackerProperties.ubuntu.timezone.availableIds.removeAll([null ])


            log.info "Loading candidates from SDKMAN!"
            jetpackerProperties.openjdk.extensions = repositoryService.loadCandidatesFromSdkMan()

            [jetpackerProperties.node,
             jetpackerProperties.guard ].each { Kit kit ->
                log.info "Updating releases for ${kit.label}"
                repositoryService.updateReleases(kit)
            }

            [jetpackerProperties.databaseEngines,
             jetpackerProperties.queueEngines,
             jetpackerProperties.searchEngines ].flatten().each { Container container ->
                log.info "Updating releases for ${container.label}"
                repositoryService.updateReleases(container)
            }
        } catch (InterruptedException|ExecutionException e) {}
    }

    JetpackerProperties load() {
        jetpackerProperties
    }
}