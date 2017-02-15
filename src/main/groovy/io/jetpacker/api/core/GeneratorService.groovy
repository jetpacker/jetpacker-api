package io.jetpacker.api.core

import groovy.util.logging.Slf4j
import io.jetpacker.api.configuration.*
import io.jetpacker.api.configuration.container.Container
import io.jetpacker.api.configuration.kit.DevelopmentKits
import io.jetpacker.api.configuration.kit.Kit
import io.jetpacker.api.configuration.machine.Machine
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
            Machine virtualMachine = jetpackerProperties.virtualMachine
            DevelopmentKits developmentKits = jetpackerProperties.developmentKits

            List<Kit> nonJavaKits = [developmentKits.node,
                                     developmentKits.node.dependency,
                                     developmentKits.node.extensions,
                                     developmentKits.guard,
                                     developmentKits.guard.dependency ].flatten()

            List<Container> containers = [jetpackerProperties.databaseServers,
                                          jetpackerProperties.messageBrokers,
                                          jetpackerProperties.searchEngines ].flatten()

            // TODO: TimeZone can be refactored for better testability
            log.info "Loading timezones"
            virtualMachine.timezone.ids = TimeZone.availableIDs.collect { String id ->
                if (!id.startsWith("SystemV"))
                    return id
            }

            virtualMachine.timezone.ids.removeAll([ null ])

            log.info "Loading candidates from SDKMAN!"
            developmentKits.openjdk.extensions = repositoryService.loadCandidatesFromSdkMan()

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