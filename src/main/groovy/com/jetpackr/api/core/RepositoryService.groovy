package com.jetpackr.api.core

import com.jetpackr.api.configuration.Option
import com.jetpackr.api.configuration.Parameter
import com.jetpackr.api.configuration.Platform
import com.jetpackr.api.configuration.Repository
import groovy.util.logging.Slf4j
import com.jetpackr.api.common.VersionComparator
import io.jetpacker.api.configuration.*
import com.jetpackr.api.configuration.kit.Kit
import com.jetpackr.api.core.response.DockerHubResponse
import com.jetpackr.api.core.response.DockerHubResult
import com.jetpackr.api.core.response.GitHubResponse
import com.jetpackr.api.core.response.NPMRegistryResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import java.util.concurrent.ExecutionException

/**
 * Created by donny on 22/11/16.
 */
@Slf4j
@Service
class RepositoryService {
    private final static VersionComparator versionComparator = new VersionComparator()

    private final RestTemplate restTemplate

    @Autowired
    RepositoryService(RestTemplate asyncRestTemplate) {
        this.restTemplate = asyncRestTemplate
    }

    void updateNonJDKReleases(Platform platform) {
        Repository repository = platform.repository

        if (repository != null) {
            if (repository.type == Repository.Type.NPMRegistry)
                platform.version.options = loadReleasesFromNPMRegistry(repository)

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

            releases.removeAll([ null, 'modified', 'created' ])
            releases.sort versionComparator

            options = releases.collect { release ->
                new Option(value: release)
            }
        }

        options
    }

    List<Option> loadReleasesFromGitHub(Repository repository) {
        ResponseEntity<List<GitHubResponse>> response =
                restTemplate.exchange(repository.url, HttpMethod.GET, null, new ParameterizedTypeReference<List<GitHubResponse>>() {})

        formatReleases(response.body*.name)
    }

    List<Option> loadReleasesFromNPMRegistry(Repository repository) {
        ResponseEntity<NPMRegistryResponse> response =
                restTemplate.exchange(repository.url, HttpMethod.GET, null, new ParameterizedTypeReference<NPMRegistryResponse>() {})

        List<String> results = response.body.time.keySet() as List<String>

        formatReleases(results)
    }


    List<Option> loadReleasesFromDockerHub(Repository repository) {
        ResponseEntity<DockerHubResponse> response =
                restTemplate.exchange(repository.url, HttpMethod.GET, null, new ParameterizedTypeReference<DockerHubResponse>() {})

        DockerHubResponse dockerHubResponse = response.body
        List<DockerHubResult> results = dockerHubResponse.results

        while (dockerHubResponse.next) {
            response =  restTemplate.exchange(dockerHubResponse.next, HttpMethod.GET, null,
                    new ParameterizedTypeReference<DockerHubResponse>() {})

            dockerHubResponse = response.body
            results += dockerHubResponse.results
        }

        formatReleases(results*.name)
    }

    void updateJDKReleases(Kit jdk) {
        log.info "Loading releases from SDKMAN!"

        String repositoryUrl = jdk.repository.url
        jdk.version.options = loadJDKOptions(repositoryUrl)
        jdk.extensions = loadSDKMANCandidates(repositoryUrl)
    }

    List<Kit> loadJDKOptions(final String repositoryUrl) throws ExecutionException, InterruptedException {
        String candidateUrl = "${repositoryUrl}/java/linux/versions/all"
        String rawReleases = restTemplate.getForEntity(candidateUrl, String.class).body
        List<String> releases = Arrays.asList(rawReleases.split(","))
        releases.sort versionComparator

        releases.collect { release ->
            new Option(value: release)
        }
    }

    List<Kit> loadSDKMANCandidates(final String repositoryUrl) throws ExecutionException, InterruptedException {
        Map<String, String> candidates = [:]

        final String candidatesListUrl = "${repositoryUrl}/list"
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

        def sdkCandidates = restTemplate.getForEntity(repositoryUrl + "/all", String.class).body

        List<Kit> kits = sdkCandidates.split(",").collect { String sdkCandidate ->
            if (sdkCandidate != "java") {
                String candidateUrl = "${repositoryUrl}/${sdkCandidate}/${sdkCandidate}/versions/all"
                String rawReleases = restTemplate.getForEntity(candidateUrl, String.class).body
                List<String> releases = Arrays.asList(rawReleases.split(","))
                releases.sort versionComparator

                log.info "Adding candidate {}", sdkCandidate

                def candidate = candidates[sdkCandidate]
                String label = candidate ? candidate['label'] : sdkCandidate
                String description = candidate ? candidate['description'] : sdkCandidate

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

        kits.removeAll([null])

        kits
    }
}