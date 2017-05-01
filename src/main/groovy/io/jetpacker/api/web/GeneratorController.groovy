package io.jetpacker.api.web

import groovy.util.logging.Slf4j
import io.jetpacker.api.configuration.JetpackerProperties
import io.jetpacker.api.core.GeneratorService
import io.jetpacker.api.web.command.JetpackerCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Created by donny on 30/10/16.
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/generator")
class GeneratorController {
    private final GeneratorService generatorService

    @Autowired
    GeneratorController(GeneratorService generatorService) {
        this.generatorService = generatorService
    }

    @GetMapping
    JetpackerProperties load() {
        generatorService.load()
    }

    @PostMapping(produces = "application/zip")
    byte[] generate(@RequestBody JetpackerCommand command) {
        generatorService.generate(command)
    }
}