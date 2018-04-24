package com.jetpackr.api.core

import freemarker.template.Configuration
import groovy.util.logging.Slf4j
import com.jetpackr.api.configuration.MyProperties
import com.jetpackr.api.configuration.container.Container
import com.jetpackr.api.configuration.container.Port
import com.jetpackr.api.configuration.kit.Extension
import com.jetpackr.api.configuration.kit.Kit
import com.jetpackr.api.configuration.kit.Kits
import com.jetpackr.api.configuration.machine.Machine
import com.jetpackr.api.web.command.MyCommand
import org.apache.tools.ant.Project
import org.apache.tools.ant.taskdefs.Zip
import org.apache.tools.ant.types.ZipFileSet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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
    private final MyProperties myProperties
    private final Configuration configuration

    private final String tmpDirectory

    @Autowired
    GeneratorService(RepositoryService repositoryService,
                     MyProperties myProperties,
                     Configuration configuration,
                     @Value("#{systemProperties['java.io.tmpdir']}/jetpackr") String tmpDirectory) {
        this.repositoryService = repositoryService
        this.myProperties = myProperties
        this.configuration = configuration
        this.tmpDirectory = tmpDirectory
    }

    @PostConstruct
    void setUp() {
        try {
            Kits kits = myProperties.kits
            List<Kit> nonJavaKits = [ kits.node, kits.ruby ]

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

            for (Container container: myProperties.containers.values()) {
                log.info "Updating releases for ${container.label}"
                repositoryService.updateNonJDKReleases(container)
            }
        } catch (InterruptedException|ExecutionException e) {
            e.printStackTrace()
        }
    }

    MyProperties load() {
        myProperties
    }

    Downloadable generate(MyCommand command) {
        List<String> templates = []
        templates << myProperties.templates.basic.files

        // TODO: Add logic for generation
        if (command.kits?.jdk)
            templates << myProperties.templates.jdk.files

        if (command.kits?.node) {
            Kit node = myProperties.kits.node
            command.kits.node.dependencyVersion = node.dependency.version.options[0].value
            templates << myProperties.templates.node.files
        }

        if (command.machine.synchronization.trim().equalsIgnoreCase("guard")) {
            Kit ruby = myProperties.kits.ruby
            command.kits.ruby.version = ruby.version.options[0].value
            templates << myProperties.templates.ruby.files
            templates << myProperties.templates.guard.files
        }

        if (command.containers?.size() > 0) {
            for (String name : command.containers.keySet()) {
                Map<String, Container> containers = myProperties.containers
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
            templates << myProperties.templates.docker.files
        }

        String generateDir = "${tmpDirectory}/${System.currentTimeMillis()}"

        File dir = new File(generateDir)
        dir.mkdirs()

        log.info "Created a temporary directory: {}", generateDir

        List<File> files = []

        for (String template: templates.flatten()) {
            String output = FreeMarkerTemplateUtils.processTemplateIntoString(
                    this.configuration.getTemplate(template),
                    command
            )

            String generateFile = "${generateDir}/${template.replaceFirst(/.ftl$/, '')}"
            File file = new File(generateFile)
            file.parentFile.mkdirs()
            file << output
            files << file

            log.info "Created a temporary file: {}", generateFile
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

        Downloadable downloadable = new Downloadable(
                dir: dir,
                file: zip.destFile
        )

        zip.reset()

        downloadable
    }
}