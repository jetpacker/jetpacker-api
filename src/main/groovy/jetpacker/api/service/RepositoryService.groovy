package jetpacker.api.service

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

    public void updateReleases(Application application) {
        if (application instanceof Kit) {
            Kit kit = (Kit) application

            if (kit.dependency)
                updateReleases(kit.dependency)
        }

        Repository repository = application.repository
        Summary summary = (Summary) application

        if (repository == Repository.GitHub)
            application.version.releases = loadReleasesFromGitHub(repository, summary)

        if (repository == Repository.DockerHub)
            application.version.releases = loadReleasesFromDockerHub(repository, summary)

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

    public String loadUrlFromRepository(Repository repository, Summary summary) {
        String url = repository.url

        if (repository && repository != Repository.SdkMan) {
            String name = summary.name
            String namespace = summary.namespace

            url = url.replace("{name}", name)

            if (namespace)
                url = url.replace("{namespace}", namespace)
        }

        url
    }

    public List<String> loadReleasesFromGitHub(Repository repository, Summary summary) {
        String url = loadUrlFromRepository(repository, summary)

        ResponseEntity<List<Summary>> response = asyncRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Summary>>() {}).get()

        List<Summary> summaries = response.body
        List<String> releases = filterReleases(summaries*.name, summary.suffix)

        releases
    }

    public List<String> loadReleasesFromDockerHub(Repository repository, Summary summary) {
        String url = loadUrlFromRepository(repository, summary)

        ResponseEntity<DockerHub> response = asyncRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<DockerHub>() {}).get()

        DockerHub dockerHub = response.body
        List<Summary> summaries = dockerHub.results

        while (dockerHub.next) {
            response =  asyncRestTemplate.exchange(dockerHub.next, HttpMethod.GET, null,
                    new ParameterizedTypeReference<DockerHub>() {}).get()

            dockerHub = response.body
            summaries += dockerHub.results
        }

        List<String> releases = filterReleases(summaries*.name, summary.suffix)

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
                            name: "${candidate}_version",
                            label: "Version"
                    )
            )
        }
    }
}
