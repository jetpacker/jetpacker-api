package jetpacker.api.core

import jetpacker.api.common.VersionComparator
import jetpacker.api.configuration.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.AsyncRestTemplate

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

    public void updateReleases(Software software) {
        if (software instanceof Kit) {
            Kit kit = (Kit) software

            if (kit.dependency)
                updateReleases(kit.dependency)
        }

        Repository repository = software.repository
        Platform platform = (Platform) software

        if (repository == Repository.GitHub)
            software.version.releases = loadReleasesFromGitHub(repository, platform)

        if (repository == Repository.DockerHub)
            software.version.releases = loadReleasesFromDockerHub(repository, platform)

    }

    public List<Version> filterReleases(List<String> releases, String suffix) {
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

    public String loadUrlFromRepository(Repository repository, Platform platform) {
        String url = repository.url

        if (repository && repository != Repository.SdkMan) {
            String name = platform.name
            String namespace = platform.namespace

            url = url.replace("{name}", name)

            if (namespace)
                url = url.replace("{namespace}", namespace)
        }

        url
    }

    public List<String> loadReleasesFromGitHub(Repository repository, Platform platform) {
        String url = loadUrlFromRepository(repository, platform)

        ResponseEntity<List<Platform>> response = asyncRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Platform>>() {}).get()

        List<Platform> platforms = response.body
        List<String> releases = filterReleases(platforms*.name, platform.suffix)

        releases
    }

    public List<String> loadReleasesFromDockerHub(Repository repository, Platform platform) {
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

    public List<Kit> loadCandidatesFromSdkMan() throws ExecutionException, InterruptedException {
        ResponseEntity<String> response = asyncRestTemplate.getForEntity(Repository.SdkMan.url, String.class).get()

        response.body.split(",").collect { String candidate ->
            ResponseEntity<String> rawVersions = asyncRestTemplate.getForEntity("${Repository.SdkMan.url}/${candidate}", String.class).get()
            List<String> versions = Arrays.asList(rawVersions.body.split(",")).reverse()
            versions.sort versionComparator

            new Kit(name: candidate,
                    label: candidate,
                    version: new Version(
                            releases: versions,
                            name: "version",
                            label: "Version"
                    )
            )
        }
    }
}
