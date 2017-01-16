package io.jetpacker.api.common

import org.apache.maven.artifact.versioning.DefaultArtifactVersion

/**
 * Created by wolf on 23/11/16.
 */
class VersionComparator implements Comparator<String> {
    @Override
    int compare(String version1, String version2) {
        return new DefaultArtifactVersion(version2)
                   .compareTo(new DefaultArtifactVersion(version1))
    }
}
