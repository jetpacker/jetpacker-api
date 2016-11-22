package jetpacker.api.controller

import jetpacker.api.configuration.Application
import jetpacker.api.configuration.Container
import jetpacker.api.configuration.Endpoint
import jetpacker.api.configuration.JetpackerProperties
import jetpacker.api.configuration.Kit
import jetpacker.api.configuration.Metadata
import jetpacker.api.configuration.Property
import jetpacker.api.configuration.Version
import jetpacker.api.service.DockerHub
import jetpacker.api.service.GeneratorService
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.AsyncRestTemplate

import javax.annotation.PostConstruct
import java.util.concurrent.ExecutionException

/**
 * Created by donny on 30/10/16.
 */
@RestController
class GeneratorController {
    private final GeneratorService generatorService
    private final JetpackerProperties jetpackerProperties
    private final AsyncRestTemplate asyncRestTemplate

    @Autowired
    GeneratorController(GeneratorService generatorService,
                        JetpackerProperties jetpackerProperties,
                        AsyncRestTemplate asyncRestTemplate) {
        this.generatorService = generatorService
        this.jetpackerProperties = jetpackerProperties
        this.asyncRestTemplate = asyncRestTemplate
    }

    @GetMapping("/load")
    JetpackerProperties load() {
        jetpackerProperties
    }

    @PostConstruct
    private void setUp() {
        try {
            jetpackerProperties.ubuntu.timezone.availableIds = TimeZone.availableIDs.collect { String id ->
                if (!id.startsWith("SystemV"))
                    return id
            }

            jetpackerProperties.ubuntu.timezone.availableIds.removeAll([ null ])

            jetpackerProperties.openjdk.extensions = retrieveSdkManCandidates()

            [ jetpackerProperties.node, jetpackerProperties.guard ].each { Kit kit ->
                updateReleases(kit)
            }

            jetpackerProperties.databaseEngines.each { Container container ->
                updateReleases(container)
            }

            jetpackerProperties.queueEngines.each { Container container ->
                updateReleases(container)
            }

            jetpackerProperties.searchEngines.each { Container container ->
                updateReleases(container)
            }
        } catch (InterruptedException|ExecutionException e) {}
    }

    private List<Kit> retrieveSdkManCandidates() throws ExecutionException, InterruptedException {
        ResponseEntity<String> response = asyncRestTemplate.getForEntity(Endpoint.SdkMan.url, String.class).get()

        response.body.split(",").collect { String candidate ->
            ResponseEntity<String> rawVersions = asyncRestTemplate.getForEntity("${Endpoint.SdkMan.url}/${candidate}", String.class).get()
            List<String> versions = Arrays.asList(rawVersions.body.split(",")).reverse()
            versions.sort versionComparator

            new Kit(name: candidate,
                    label: candidate,
                    version: new Version(
                            releases: versions,
                            property: new Property(
                                    name: "${candidate}_version",
                                    label: "Version"
                            )
                    )
            )
        }
    }

    private void updateReleases(Application application, Boolean dependency = false) {
        if (application instanceof Kit) {
            Kit kit = (Kit) application

            if (kit.dependency)
                updateReleases(kit.dependency, true)
        }

        Endpoint endpoint = application.endpoint

        if (endpoint && endpoint != Endpoint.SdkMan) {
            String url = endpoint.url.replace("{name}", application.name)

            if (application.namespace)
                url = url.replace("{namespace}", application.namespace)

            List<Metadata> metadataList = null

            if (endpoint == Endpoint.GitHub) {
                ResponseEntity<List<Metadata>> response = asyncRestTemplate.exchange(url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<Metadata>>() {}).get()
                metadataList = response.body
            }

            if (endpoint == Endpoint.DockerHub) {
                ResponseEntity<DockerHub> response = asyncRestTemplate.exchange(url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<DockerHub>() {}).get()
                DockerHub dockerHub = response.body
                metadataList = dockerHub.results

                while (dockerHub.next) {
                    response =  asyncRestTemplate.exchange(dockerHub.next, HttpMethod.GET, null,
                            new ParameterizedTypeReference<DockerHub>() {}).get()
                    dockerHub = response.body
                    metadataList += dockerHub.results
                }

            }

            if (metadataList) {
                List<String> versions = metadataList.collect { Metadata metadata ->
                    String pattern = "^v?[0-9]+([\\._][0-9]+)*(${application.suffix})?\$"

                    if (metadata.name =~ pattern)
                        metadata.name
                }

                versions.removeAll([ null ])
                versions.sort versionComparator

                if (!dependency)
                    application.version.releases = versions
            }
        }
    }

    private def versionComparator = { String a, String b ->
        new DefaultArtifactVersion(b) <=> new DefaultArtifactVersion(a)
    }
}