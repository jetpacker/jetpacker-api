package io.jetpacker.api.core

import freemarker.template.Configuration
import groovy.util.logging.Slf4j
import io.jetpacker.api.configuration.JetpackerProperties
import io.jetpacker.api.configuration.container.Container
import io.jetpacker.api.configuration.kit.Kit
import io.jetpacker.api.configuration.kit.Kits
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Service

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

    private final Resource[] resources

    @Autowired
    GeneratorService(RepositoryService repositoryService,
                     JetpackerProperties jetpackerProperties,
                     Configuration configuration) {
        this.repositoryService = repositoryService
        this.jetpackerProperties = jetpackerProperties
        this.configuration = configuration

        this.resources = new PathMatchingResourcePatternResolver()
                                .getResources("classpath*:/templates/**/*.ftl")
    }

    @PostConstruct
    void setUp() {
        try {
            Kits kits = jetpackerProperties.kits

            List<Kit> nonJavaKits = [ kits.node,
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

    void generate() {
        // TODO: Add logic for generation

//        Template template = this.configuration.getTemplate("Vagrantfile.ftl")
//
//
//        def model = [
//                machine: [
//                        box: "okay",
//                        memory: "2133"
//                ]
//        ]
//
//        String output = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
//
//        println "resources: ${resources[0].file.absolutePath}"
//
//        TODO: resources: /Users/wolf/Workspace/jetpacker/jetpacker-api/build/resources/main/templates/Vagrantfile.ftl
        // use regex groups to filter out the templates (get $3):
        // (dir/and/their/sub/dir/)(templates/)(([a-zA-Z0-9]+/)+[a-zA-Z0-9]+(.yml)?.ftl)

//        println "configuration: ${configuration}"
//        println "output: ${output}"
    }
}