package io.jetpacker.api.configuration.machine

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.jetpacker.api.configuration.Option
import io.jetpacker.api.configuration.Parameter

/**
 * Created by donny on 15/11/16.
 */
@Slf4j
@CompileStatic
class Timezone extends Parameter {
    Timezone() {
        log.info "Loading timezones"

        options = []

        TimeZone.availableIDs.each { String id ->
            if (!id.startsWith("SystemV"))
                options << new Option(value: id)
        }
    }
}