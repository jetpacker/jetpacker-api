package io.jetpacker.api.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import freemarker.template.Configuration
import groovy.util.logging.Slf4j
import io.jetpacker.api.configuration.JetpackerProperties
import io.jetpacker.api.configuration.container.Container
import io.jetpacker.api.configuration.container.Port
import io.jetpacker.api.configuration.kit.Extension
import io.jetpacker.api.configuration.kit.Kit
import io.jetpacker.api.configuration.kit.Kits
import io.jetpacker.api.web.command.JetpackerCommand
import org.apache.tools.ant.Project
import org.apache.tools.ant.taskdefs.Zip
import org.apache.tools.ant.types.ZipFileSet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Service
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils
import org.springframework.util.StreamUtils

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

    private final String pattern = /(\/.*\/+freemarker\/)(.*\.ftl)$/
    private final List<String> templates = new ArrayList<>()

    private final String tmpDirectory

    @Autowired
    GeneratorService(RepositoryService repositoryService,
                     JetpackerProperties jetpackerProperties,
                     Configuration configuration,
                     @Value("#{systemProperties['java.io.tmpdir']}/jetpacker") String tmpDirectory) {
        this.repositoryService = repositoryService
        this.jetpackerProperties = jetpackerProperties
        this.configuration = configuration
        this.tmpDirectory = tmpDirectory
    }

    @PostConstruct
    void setUp() {
        for (Resource resource: new PathMatchingResourcePatternResolver()
                .getResources("classpath*:/freemarker/**/*.ftl")) {
            String path = resource.file.absolutePath.replaceAll("\\\\", "/")
            def m = path =~ pattern
            templates.add(m[0][2])
        }

        try {
            Kits kits = jetpackerProperties.kits
            List<Kit> nonJavaKits = [ kits.node, kits.guard ]

            repositoryService.updateJDKReleases(kits.jdk)

            for (Kit kit: nonJavaKits) {
                log.info "Updating releases for ${kit.label}"
                repositoryService.updateNonJDKReleases(kit)

                if (kit.dependency) {
                    log.info "Updating releases for ${kit.dependency.label}"
                    repositoryService.updateNonJDKReleases(kit.dependency)
                }

                if (kit.extensions && kit.extensions.size() > 0)
                    for (Extension extension: kit.extensions) {
                        log.info "Updating releases for ${extension.label}"
                        repositoryService.updateNonJDKReleases(extension)
                    }
            }

            for (Container container: jetpackerProperties.containers.values()) {
                log.info "Updating releases for ${container.label}"
                repositoryService.updateNonJDKReleases(container)
            }
        } catch (InterruptedException|ExecutionException e) {
            e.printStackTrace()
        }
    }

    JetpackerProperties load() {
        jetpackerProperties
    }

    File generate(JetpackerCommand command) {
        ObjectMapper mapper = new ObjectMapper()
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true)

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
            for (String name : command.containers.keySet()) {
                Map<String, Container> containers = jetpackerProperties.containers
                command.containers[name].command = containers[name].command

                Map<String, String> environment = containers[name].environment

                if (environment) {
                    if (!command.containers[name].environment)
                        command.containers[name].environment = [:]

                    command.containers[name].environment.putAll(environment)
                }

                List<Port> ports = containers[name].ports

                if (ports && ports.size() > 0) {
                    for (Port port: ports) {
                        if (!command.containers[name].ports)
                            command.containers[name].ports = [:]

                        command.containers[name].ports[port.host] = port.container
                    }
                }

                Map<String, String> volumes = containers[name].volumes
                if (volumes && volumes.size() > 0) {
                    volumes.each { key, value ->
                        if (!command.containers[name].volumes)
                            command.containers[name].volumes = []

                        if (!command.namedVolumes)
                            command.namedVolumes = []

                        command.containers[name].volumes << "${key}:${value}"
                        command.namedVolumes << key
                    }
                }
            }
        }

        String generateDir = "${tmpDirectory}/${System.currentTimeMillis()}"
        log.info "Create temporary directory: {}", generateDir
        File dir = new File(generateDir)
        dir.mkdirs()

        List<File> files = new ArrayList<>()

        for (String template: templates) {
            log.info "Create temporary file: {}", template
            String output = FreeMarkerTemplateUtils.processTemplateIntoString(
                    this.configuration.getTemplate(template),
                    command
            )

            String generateFile = "${generateDir}/${template.replaceFirst(/.ftl$/, '')}"
            log.info "Create temporary file: {}", generateFile
            File file = new File(generateFile)
            file.parentFile.mkdirs()
            file << output
            files << file
        }

        Zip zip = new Zip()
        zip.project = new Project()
        zip.defaultexcludes = false

        ZipFileSet set = new ZipFileSet()
        set.dir = dir
        set.fileMode = "755"
        set.defaultexcludes = false

        zip.addFileset(set)

        zip.destFile = new File(dir.absolutePath + "/jetpack.zip")
        zip.execute()

        log.info "Created a temporary zip file: {}", zip.destFile.absolutePath

        zip.destFile
    }
}