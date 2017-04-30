package io.jetpacker.api.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import freemarker.template.Configuration
import freemarker.template.Template
import groovy.util.logging.Slf4j
import io.jetpacker.api.configuration.JetpackerProperties
import io.jetpacker.api.configuration.container.Container
import io.jetpacker.api.configuration.container.Port
import io.jetpacker.api.configuration.container.Volume
import io.jetpacker.api.configuration.kit.Kit
import io.jetpacker.api.configuration.kit.Kits
import io.jetpacker.api.web.command.JetpackerCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils

import javax.annotation.PostConstruct
import java.util.concurrent.ExecutionException

/**
 * Created by donny on 22/11/16.
 */
@Slf4j
@Service
class GeneratorService {
    private final RepositoryService repositoryService
    private final JetpackerProperties jetpackerProperties
    private final Configuration configuration

    private final String pattern = /^(\/.*\/+templates\/)(.*\.ftl)$/
    private final List<Template> templates = new ArrayList<>()

    @Autowired
    GeneratorService(RepositoryService repositoryService,
                     JetpackerProperties jetpackerProperties,
                     Configuration configuration) {
        this.repositoryService = repositoryService
        this.jetpackerProperties = jetpackerProperties
        this.configuration = configuration
    }

    @PostConstruct
    void setUp() {
        for (Resource resource: new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/templates/**/*.ftl")) {
            def m = resource.file.absolutePath =~ pattern
            Template template = this.configuration.getTemplate(m[0][2])
            templates.add(template)
        }

        try {
            Kits kits = jetpackerProperties.kits

            List<Kit> nonJavaKits = [kits.node,
                                     kits.node.dependency,
                                     kits.node.extensions,
                                     kits.guard,
                                     kits.guard.dependency ].flatten()

            log.info "Loading candidates from SDKMAN!"
            kits.openjdk.extensions = repositoryService.loadCandidatesFromSdkMan()

            nonJavaKits.each { Kit kit ->
                log.info "Updating releases for ${kit.label}"
                repositoryService.updateReleases(kit)
            }

            jetpackerProperties.containers.values().each { Container container ->
                log.info "Updating releases for ${container.label}"
                repositoryService.updateReleases(container)
            }
        } catch (InterruptedException|ExecutionException e) {
            e.printStackTrace()
        }
    }

    JetpackerProperties load() {
        jetpackerProperties
    }

    void generate(JetpackerCommand command) {
        ObjectMapper mapper = new ObjectMapper()
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true)

        log.info "before body:\n {}", mapper.writeValueAsString(command)

        // TODO: Add logic for generation
        if (command.kits.node) {
            Kit node = jetpackerProperties.kits.node
            command.kits.node.dependencyVersion = node.dependency.version.options[0].value
        }

        if (command.kits.guard) {
            Kit guard = jetpackerProperties.kits.guard
            command.kits.guard.dependencyVersion = guard.dependency.version.options[0].value
        }

        if (command.containers) {
            command.containers.keySet().each { String name ->
                Map<String, Container> containers = jetpackerProperties.containers
                command.containers[name].command = containers[name].command

                if (containers[name].env) {
                    if (!command.containers[name].env)
                        command.containers[name].env = new HashMap<>()

                    command.containers[name].env.putAll(containers[name].env)
                }

                containers[name].ports.each { Port port ->
                    if (!command.containers[name].ports)
                        command.containers[name].ports = new HashMap<>()

                    command.containers[name].ports[port.host] = port.container
                }

                containers[name].volumes.each { Volume volume ->
                    if (!command.containers[name].volumes)
                        command.containers[name].volumes = new HashMap<>()

                    command.containers[name].volumes[volume.host] = volume.container
                }
            }
        }

        log.info "after body:\n {}", mapper.writeValueAsString(command)


        for (Template template: templates) {
            String output = FreeMarkerTemplateUtils.processTemplateIntoString(template, command)
            log.info "configuration: {}",  configuration
            println "output: ${output}"
        }
    }
}