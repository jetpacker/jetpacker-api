package jetpacker.api.core

import jetpacker.api.configuration.Container
import jetpacker.api.configuration.JetpackerProperties
import jetpacker.api.configuration.Kit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import java.util.concurrent.ExecutionException

/**
 * Created by donny on 22/11/16.
 */
@Service
class PropertiesService {
    private final JetpackerProperties jetpackerProperties
    private final RepositoryService repositoryService

    @Autowired
    PropertiesService(RepositoryService repositoryService,
                      JetpackerProperties jetpackerProperties) {
        this.repositoryService = repositoryService
        this.jetpackerProperties = jetpackerProperties
    }

    @PostConstruct
    public void setUp() {
        try {
            jetpackerProperties.ubuntu.timezone.availableIds = TimeZone.availableIDs.collect { String id ->
                if (!id.startsWith("SystemV"))
                    return id
            }

            jetpackerProperties.ubuntu.timezone.availableIds.removeAll([ null ])
            jetpackerProperties.openjdk.extensions = repositoryService.loadCandidatesFromSdkMan()

            [ jetpackerProperties.node, jetpackerProperties.guard ].each { Kit kit ->
                repositoryService.updateReleases(kit)
            }

            jetpackerProperties.databaseEngines.each { Container container ->
                repositoryService.updateReleases(container)
            }

            jetpackerProperties.queueEngines.each { Container container ->
                repositoryService.updateReleases(container)
            }

            jetpackerProperties.searchEngines.each { Container container ->
                repositoryService.updateReleases(container)
            }
        } catch (InterruptedException|ExecutionException e) {}
    }

    public JetpackerProperties load() {
        jetpackerProperties
    }
}