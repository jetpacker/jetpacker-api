package io.jetpacker.api.core

import groovy.util.logging.Slf4j
import io.jetpacker.api.common.VersionComparator
import io.jetpacker.api.configuration.*
import io.jetpacker.api.configuration.kit.Kit
import io.jetpacker.api.core.response.DockerHubResponse
import io.jetpacker.api.core.response.GitHubResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

import java.util.concurrent.ExecutionException

/**
 * Created by donny on 22/11/16.
 */
@Slf4j
@Service
class RepositoryService {
    private final static String SDKMAN_CANDIDATES_URL = "http://api.sdkman.io/candidates"
    private final static VersionComparator versionComparator = new VersionComparator()

    private final RestTemplate restTemplate

    @Autowired
    RepositoryService(RestTemplate asyncRestTemplate) {
        this.restTemplate = asyncRestTemplate
    }

    void updateReleases(Platform platform) { // to Platform... platforms
        Repository repository = platform.repository

        if (repository != null) {
            if (repository.type == Repository.Type.GitHub)
                platform.version.options = loadReleasesFromGitHub(repository)

            if (repository.type == Repository.Type.DockerHub)
                platform.version.options = loadReleasesFromDockerHub(repository)
        }
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

    List<Option> loadReleasesFromGitHub(Repository repository) {
        ResponseEntity<List<GitHubResponse>> response =
                restTemplate.exchange(repository.url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Metadata>>() {})

        formatReleases(response.body*.name)
    }

    List<Option> loadReleasesFromDockerHub(Repository repository) {
        ResponseEntity<DockerHubResponse> response =
                restTemplate.exchange(repository.url, HttpMethod.GET, null, new ParameterizedTypeReference<DockerHubResponse>() {})

        DockerHubResponse dockerHubResponse = response.body
        List<Metadata> results = dockerHubResponse.results

        while (dockerHubResponse.next) {
            response =  restTemplate.exchange(dockerHubResponse.next, HttpMethod.GET, null,
                    new ParameterizedTypeReference<DockerHubResponse>() {})

            dockerHubResponse = response.body
            results += dockerHubResponse.results
        }

        formatReleases(results*.name)
    }

    List<Kit> loadCandidatesFromSdkMan() throws ExecutionException, InterruptedException {
        Map<String, String> candidates = [:]

        final String candidatesListUrl = "${SDKMAN_CANDIDATES_URL}/list"
        String sdkLabels = restTemplate.getForEntity(candidatesListUrl, String.class).body

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

        def sdkCandidates = restTemplate.getForEntity(SDKMAN_CANDIDATES_URL, String.class).body

        sdkCandidates.split(",").collect { String sdkCandidate ->
            String candidateUrl = "${SDKMAN_CANDIDATES_URL}/${sdkCandidate}"
            String rawReleases = restTemplate.getForEntity(candidateUrl, String.class).body
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