package io.jetpacker.api.core

import groovy.util.logging.Slf4j
import io.jetpacker.api.common.VersionComparator
import io.jetpacker.api.configuration.*
import io.jetpacker.api.configuration.kit.Kit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.AsyncRestTemplate
import org.springframework.web.util.UriComponentsBuilder

import java.util.concurrent.ExecutionException

/**
 * Created by donny on 22/11/16.
 */
@Slf4j
@Service
class RepositoryService {
    private final VersionComparator versionComparator = new VersionComparator()

    private final AsyncRestTemplate asyncRestTemplate

    @Autowired
    RepositoryService(AsyncRestTemplate asyncRestTemplate) {
        this.asyncRestTemplate = asyncRestTemplate
    }

    void updateReleases(Platform platform) {
        Repository repository = platform.repository
        Metadata metadata = (Metadata) platform

        if (repository == Repository.GitHub)
            platform.version.options = loadReleasesFromGitHub(repository, metadata)

        if (repository == Repository.DockerHub)
            platform.version.options = loadReleasesFromDockerHub(repository, metadata)
    }

    List<Option> formatReleases(List<String> releases) {
        List<Option> options = null

        if (releases) {
            releases = releases.collect { String release ->
                String pattern = "^v?[0-9]+([\\._][0-9]+)*\$"

                if (release =~ pattern) {
                    release.replaceFirst("v","")
                           .replace("_", ".")
                }
            }

            releases.removeAll([ null ])
            releases.sort versionComparator

            options = releases.collect { release ->
                new Option(value: release)
            }
        }

        options
    }

    String loadUrlFromRepository(Repository repository, Metadata metadata) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(repository.url)
        Map<String, String> parameters = [:]

        if (repository != Repository.SdkMan) {
            parameters['name'] = metadata.alias?: metadata.name

            if (metadata.namespace)
                parameters['namespace'] = metadata.namespace
        }

        builder.buildAndExpand(parameters).toUriString()
    }

    List<Option> loadReleasesFromGitHub(Repository repository, Metadata metadata) {
        String url = loadUrlFromRepository(repository, metadata)

        ResponseEntity<List<Metadata>> response = asyncRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Metadata>>() {}).get()

        formatReleases(response.body*.name)
    }

    List<Option> loadReleasesFromDockerHub(Repository repository, Metadata metadata) {
        String url = loadUrlFromRepository(repository, metadata)

        ResponseEntity<DockerHub> response = asyncRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<DockerHub>() {}).get()

        DockerHub dockerHub = response.body
        List<Metadata> results = dockerHub.results

        while (dockerHub.next) {
            response =  asyncRestTemplate.exchange(dockerHub.next, HttpMethod.GET, null,
                    new ParameterizedTypeReference<DockerHub>() {}).get()

            dockerHub = response.body
            results += dockerHub.results
        }

        formatReleases(results*.name)
    }

    List<Kit> loadCandidatesFromSdkMan() throws ExecutionException, InterruptedException {
        Map<String, String> candidates = [:]

        String candidatesUrl = "${Repository.SdkMan.url}/list"
        String sdkLabels = asyncRestTemplate.getForEntity(candidatesUrl, String.class).get().body

        sdkLabels.split(/(?m)^(-)+$/).eachWithIndex{ String sdkLabel, int i ->
            if (i > 0 && i < sdkLabel.size() - 1) {
                String[] tokens = sdkLabel.split(/(?m)^\s*$/)

                String name = tokens[2].replace('$ sdk install ', "").trim()
                String label = tokens[0].replaceFirst(/\s+\(.+\)/, "")
                                        .replaceFirst(/\s+http(s)?:\\/\\/.+$/, "")
                                        .trim()

                String description = tokens[1].replaceAll(/\n/, " ").trim()

                candidates[name] = [
                        label: label,
                        description: description
                ]
            }
        }

        def sdkCandidates = asyncRestTemplate.getForEntity(Repository.SdkMan.url, String.class).get().body

        sdkCandidates.split(",").collect { String sdkCandidate ->
            String candidateUrl = "${Repository.SdkMan.url}/${sdkCandidate}"
            String rawReleases = asyncRestTemplate.getForEntity(candidateUrl, String.class).get().body
            List<String> releases = Arrays.asList(rawReleases.split(","))
            releases.sort versionComparator

            log.info "Adding candidate {}", sdkCandidate

            def candidate = candidates[sdkCandidate]
            String label = candidate? candidate['label'] : sdkCandidate
            String description = candidate? candidate['description'] : sdkCandidate

            new Kit(name: sdkCandidate,
                    label: label,
                    description: description,
                    version: new Parameter(
                            options: releases.collect { release ->
                                new Option(value: release)
                            },
                            name: "version",
                            label: "Version"
                    )
            )
        }
    }
}