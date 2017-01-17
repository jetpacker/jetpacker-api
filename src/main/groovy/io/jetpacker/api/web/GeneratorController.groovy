package io.jetpacker.api.web

import io.jetpacker.api.configuration.JetpackerProperties
import io.jetpacker.api.core.GeneratorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by donny on 30/10/16.
 */
@RestController
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
}