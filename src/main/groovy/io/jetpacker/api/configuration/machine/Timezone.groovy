package io.jetpacker.api.configuration.machine

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.jetpacker.api.configuration.Parameter

/**
 * Created by donny on 15/11/16.
 */
@Slf4j
@CompileStatic
class Timezone extends Parameter {
    final List<String> ids

    Timezone() {
        // TODO: TimeZone can be refactored for better testability
        log.info "Loading timezones"
        ids = TimeZone.availableIDs.collect { String id ->
            if (!id.startsWith("SystemV"))
                return id
        }

        ids.removeAll([ null ])
    }
}