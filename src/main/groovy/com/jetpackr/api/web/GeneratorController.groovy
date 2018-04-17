package com.jetpackr.api.web

import groovy.util.logging.Slf4j
import com.jetpackr.api.configuration.MyProperties
import com.jetpackr.api.core.GeneratorService
import com.jetpackr.api.web.command.MyCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletResponse

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
    MyProperties load() {
        generatorService.load()
    }

    @PostMapping
    void generate(@RequestBody MyCommand command, HttpServletResponse response) {
        File file = generatorService.generate(command)
        response.contentType = "application/zip"
        response.setHeader("Content-disposition", "attachment; filename=jetpack.zip")
        response.setHeader("Content-Length", String.valueOf(file.length()))

        // copy from in to out
        FileCopyUtils.copy(
                new FileInputStream(file),
                response.outputStream
        )

        response.flushBuffer()
    }
}