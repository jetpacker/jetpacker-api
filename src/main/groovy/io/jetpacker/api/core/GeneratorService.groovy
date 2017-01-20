package io.jetpacker.api.core

import groovy.util.logging.Slf4j
import io.jetpacker.api.configuration.Container
import io.jetpacker.api.configuration.JetpackerProperties
import io.jetpacker.api.configuration.Kit
import io.jetpacker.api.configuration.Machine
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
            Machine ubuntu = jetpackerProperties.ubuntu

            Kit openjdk = jetpackerProperties.openjdk
            Kit node = jetpackerProperties.node
            Kit guard = jetpackerProperties.guard

            List<Kit> nonJavaKits = [ node, node.dependency, node.extensions, guard, guard.dependency ].flatten()

            List<Container> databaseServers = jetpackerProperties.databaseServers
            List<Container> messageBrokers = jetpackerProperties.messageBrokers
            List<Container> searchEngines = jetpackerProperties.searchEngines

            List<Kit> containers = [ databaseServers, messageBrokers, searchEngines ].flatten()

            // TODO: TimeZone can be refactored for better testability
            log.info "Loading timezones"
            ubuntu.timezone.availableIds = TimeZone.availableIDs.collect { String id ->
                if (!id.startsWith("SystemV"))
                    return id
            }

            ubuntu.timezone.availableIds.removeAll([ null ])

            log.info "Loading candidates from SDKMAN!"
            openjdk.extensions = repositoryService.loadCandidatesFromSdkMan()

            nonJavaKits.each { Kit kit ->
                log.info "Updating releases for ${kit.label}"
                repositoryService.updateReleases(kit)
            }

            containers.each { Container container ->
                log.info "Updating releases for ${container.label}"
                repositoryService.updateReleases(container)
            }
        } catch (InterruptedException|ExecutionException e) {
            e.printStackTrace()
        }
    }

    JetpackerProperties load() {
        jetpackerProperties
    }
}