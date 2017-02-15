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

    void updateReleases(Software software) {
        Repository repository = software.repository
        Platform platform = (Platform) software

        if (repository == Repository.GitHub)
            software.version.releases = loadReleasesFromGitHub(repository, platform)

        if (repository == Repository.DockerHub)
            software.version.releases = loadReleasesFromDockerHub(repository, platform)
    }

    List<Version> filterReleases(List<String> releases, String suffix) {
        if (releases) {
            releases = releases.collect { String release ->
                String pattern = "^v?[0-9]+([\\._][0-9]+)*(${suffix})?\$"

                if (release =~ pattern)
                    release
            }

            releases.removeAll([ null ])
            releases.sort versionComparator
        }

        releases
    }

    String loadUrlFromRepository(Repository repository, Platform platform) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(repository.url)
        Map<String, String> parameters = [:]

        if (repository != Repository.SdkMan) {
            if (platform.name)
                parameters['name'] = platform.name

            if (platform.namespace)
                parameters['namespace'] = platform.namespace
        }

        builder.buildAndExpand(parameters).toUriString()
    }

    List<String> loadReleasesFromGitHub(Repository repository, Platform platform) {
        String url = loadUrlFromRepository(repository, platform)

        ResponseEntity<List<Platform>> response = asyncRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Platform>>() {}).get()

        List<Platform> platforms = response.body
        List<String> releases = filterReleases(platforms*.name, platform.suffix)

        releases
    }

    List<String> loadReleasesFromDockerHub(Repository repository, Platform platform) {
        String url = loadUrlFromRepository(repository, platform)

        ResponseEntity<DockerHub> response = asyncRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<DockerHub>() {}).get()

        DockerHub dockerHub = response.body
        List<Platform> platforms = dockerHub.results

        while (dockerHub.next) {
            response =  asyncRestTemplate.exchange(dockerHub.next, HttpMethod.GET, null,
                    new ParameterizedTypeReference<DockerHub>() {}).get()

            dockerHub = response.body
            platforms += dockerHub.results
        }

        List<String> releases = filterReleases(platforms*.name, platform.suffix)

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
