package com.jetpackr.api.core

import groovy.util.logging.Slf4j
import org.springframework.util.FileSystemUtils

@Slf4j
class Downloadable {
    File dir
    File file

    void delete() {
        log.info "Delete tmp directory: {}", dir
        FileSystemUtils.deleteRecursively(dir)
    }
}
