package io.jetpacker.api.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.util.logging.Slf4j
import io.jetpacker.api.configuration.JetpackerProperties
import io.jetpacker.api.core.GeneratorService
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

    @PostMapping
    void generate(@RequestBody Map<Object, Object> body) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        log.info "body: ${mapper.writeValueAsString(body)}";
    }
}