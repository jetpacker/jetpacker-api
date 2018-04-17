package com.jetpackr.api.configuration.machine

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import com.jetpackr.api.configuration.Option
import com.jetpackr.api.configuration.Parameter

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