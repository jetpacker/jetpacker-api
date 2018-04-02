package io.jetpacker.api.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import freemarker.template.Configuration
import groovy.util.logging.Slf4j
import io.jetpacker.api.configuration.JetpackerProperties
import io.jetpacker.api.configuration.container.Container
import io.jetpacker.api.configuration.container.Port
import io.jetpacker.api.configuration.container.Volume
import io.jetpacker.api.configuration.kit.Kit
import io.jetpacker.api.configuration.kit.Kits
import io.jetpacker.api.web.command.DataContainer
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

    private final String pattern = /(\/.*\/+templates\/)(.*\.ftl)$/
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
                .getResources("classpath*:/templates/**/*.ftl")) {
            String path = resource.file.absolutePath.replaceAll("\\\\", "/")
            def m = path =~ pattern
            templates.add(m[0][2])
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


            for (Kit kit: nonJavaKits) {
                log.info "Updating releases for ${kit.label}"
                repositoryService.updateReleases(kit)
            }

            for (Container container: jetpackerProperties.containers.values()) {
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

    byte[] generate(JetpackerCommand command) {
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
            command.containers.keySet().each { String name ->
                Map<String, Container> containers = jetpackerProperties.containers
                command.containers[name].command = containers[name].command

                Map<String, String> env = containers[name].env

                if (env) {
                    if (!command.containers[name].env)
                        command.containers[name].env = new LinkedHashMap<>()

                    command.containers[name].env.putAll(env)
                }

                List<Port> ports = containers[name].ports

                if (ports && ports.size() > 0) {
                    ports.each { Port port ->
                        if (!command.containers[name].ports)
                            command.containers[name].ports = new LinkedHashMap<>()

                        command.containers[name].ports[port.host] = port.container
                    }
                }

                List<Volume> volumes = containers[name].volumes

                if (volumes && volumes.size() > 0) {
                    if (!command.dataContainer) {
                        command.dataContainer = new DataContainer(
                                name: jetpackerProperties.dataContainer.name,
                                version: jetpackerProperties.dataContainer.version.value
                        )
                    }

                    command.containers[name].volumesFrom = command.dataContainer.name

                    volumes.each { Volume volume ->
                        if (!command.dataContainer.volumes)
                            command.dataContainer.volumes = new LinkedHashMap<>()

                        command.dataContainer.volumes[volume.host] = volume.container
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

        byte[] bytes = StreamUtils.copyToByteArray(new FileInputStream(zip.destFile))

        dir.deleteDir()

        bytes
    }
}