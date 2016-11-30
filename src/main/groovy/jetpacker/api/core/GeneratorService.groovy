package jetpacker.api.core

import groovy.util.logging.Slf4j
import jetpacker.api.configuration.Container
import jetpacker.api.configuration.Jetpacker
import jetpacker.api.configuration.Kit
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
    private final Jetpacker jetpacker
    private final RepositoryService repositoryService

    @Autowired
    GeneratorService(RepositoryService repositoryService,
                     Jetpacker jetpacker) {
        this.repositoryService = repositoryService
        this.jetpacker = jetpacker
    }

    @PostConstruct
    public void setUp() {
        try {
            log.info "Loading timezones"
            jetpacker.ubuntu.timezone.availableIds = TimeZone.availableIDs.collect { String id ->
                if (!id.startsWith("SystemV"))
                    return id
            }

            jetpacker.ubuntu.timezone.availableIds.removeAll([null ])

            log.info "Loading candidates from SDKMAN!"
            jetpacker.openjdk.extensions = repositoryService.loadCandidatesFromSdkMan()

            [ jetpacker.node,
              jetpacker.guard ].each { Kit kit ->
                log.info "Updating releases for ${kit.label}"
                repositoryService.updateReleases(kit)
            }

            [ jetpacker.databaseEngines,
              jetpacker.queueEngines,
              jetpacker.searchEngines ].flatten().each { Container container ->
                log.info "Updating releases for ${container.label}"
                repositoryService.updateReleases(container)
            }
        } catch (InterruptedException|ExecutionException e) {}
    }

    public Jetpacker load() {
        jetpacker
    }
}