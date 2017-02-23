package io.jetpacker.api.core

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
            platform.version.releases = loadReleasesFromGitHub(repository, metadata)

        if (repository == Repository.DockerHub)
            platform.version.releases = loadReleasesFromDockerHub(repository, metadata)
    }

    List<String> filterReleases(List<String> releases) {
        if (releases) {
            releases = releases.collect { String release ->
                String pattern = "^v?[0-9]+([\\._][0-9]+)*\$"

                if (release =~ pattern) {
                    release = release.replaceFirst("v","")
                                     .replace("_", ".");
                    release
                }
            }

            releases.removeAll([ null ])
            releases.sort versionComparator
        }

        releases
    }

    String loadUrlFromRepository(Repository repository, Metadata metadata) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(repository.url)
        Map<String, String> parameters = [:]

        if (repository != Repository.SdkMan) {
            if (metadata.name)
                parameters['name'] = metadata.name

            if (metadata.namespace)
                parameters['namespace'] = metadata.namespace
        }

        builder.buildAndExpand(parameters).toUriString()
    }

    List<String> loadReleasesFromGitHub(Repository repository, Metadata metadata) {
        String url = loadUrlFromRepository(repository, metadata)

        ResponseEntity<List<Metadata>> response = asyncRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Metadata>>() {}).get()

        List<String> releases = filterReleases(response.body*.name)

        releases
    }

    List<String> loadReleasesFromDockerHub(Repository repository, Metadata metadata) {
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

        List<String> releases = filterReleases(results*.name)

        releases
    }

    List<Kit> loadCandidatesFromSdkMan() throws ExecutionException, InterruptedException {
        Map<String, String> candidates = [:]

        String candidatesUrl = "${Repository.SdkMan.url}/list"
        String rawCandidates = asyncRestTemplate.getForEntity(candidatesUrl, String.class).get().body

        rawCandidates.split(/(?m)^(-)+$/).eachWithIndex{ String rawCandidate, int i ->
            if (i > 0 && i < rawCandidate.size() - 1) {
                String[] tokens = rawCandidate.split(/(?m)^\s*$/)

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

        rawCandidates = asyncRestTemplate.getForEntity(Repository.SdkMan.url, String.class).get().body

        rawCandidates.split(",").collect { String candidate ->
            String candidateUrl = "${Repository.SdkMan.url}/${candidate}"
            String rawReleases = asyncRestTemplate.getForEntity(candidateUrl, String.class).get().body
            List<String> releases = Arrays.asList(rawReleases.split(","))
            releases.sort versionComparator

            new Kit(name: candidate,
                    label: candidates[candidate]['label'],
                    description: candidates[candidate]['description'],
                    version: new Version(
                            releases: releases,
                            name: "version",
                            label: "Version"
                    )
            )
        }
    }
}
