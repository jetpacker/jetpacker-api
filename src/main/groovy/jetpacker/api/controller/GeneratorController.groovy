package jetpacker.api.controller

import jetpacker.api.cache.Jetpacker
import jetpacker.api.service.CacheService
import jetpacker.api.service.GeneratorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created by donny on 30/10/16.
 */
@RestController
class GeneratorController {
    private final CacheService cacheService
    private final GeneratorService generatorService

    @Autowired
    GeneratorController(CacheService cacheService, GeneratorService generatorService) {
        this.cacheService = cacheService
        this.generatorService = generatorService
    }

    @GetMapping("/items")
    Jetpacker items() {
        cacheService.items()
    }
}
