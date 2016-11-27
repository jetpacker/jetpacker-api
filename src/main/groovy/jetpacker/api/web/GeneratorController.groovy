package jetpacker.api.web

import jetpacker.api.configuration.JetpackerProperties

import jetpacker.api.core.GeneratorService
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

    @GetMapping("/load")
    JetpackerProperties load() {
        generatorService.load()
    }
}