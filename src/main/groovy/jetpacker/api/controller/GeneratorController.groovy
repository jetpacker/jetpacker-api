package jetpacker.api.controller

import jetpacker.api.configuration.JetpackerProperties
import jetpacker.api.service.GeneratorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by donny on 30/10/16.
 */
@RestController
class GeneratorController {
    private final GeneratorService generatorService
    private final JetpackerProperties jetpackerProperties

    @Autowired
    GeneratorController(GeneratorService generatorService, JetpackerProperties jetpackerProperties) {
        this.generatorService = generatorService
        this.jetpackerProperties = jetpackerProperties
    }

    @GetMapping("/load")
    JetpackerProperties load() {
        jetpackerProperties
    }
}
