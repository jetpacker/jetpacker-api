package jetpacker.api.web

import jetpacker.api.configuration.JetpackerProperties
import jetpacker.api.core.GeneratorService
import jetpacker.api.core.PropertiesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by donny on 30/10/16.
 */
@RestController
class GeneratorController {
    private final GeneratorService generatorService
    private final PropertiesService propertiesService

    @Autowired
    GeneratorController(GeneratorService generatorService,
                        PropertiesService propertiesService) {
        this.generatorService = generatorService
        this.propertiesService = propertiesService
    }

    @GetMapping("/load")
    JetpackerProperties load() {
        propertiesService.load()
    }
}