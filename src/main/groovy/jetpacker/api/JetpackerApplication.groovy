package jetpacker.api

import groovy.transform.CompileStatic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
@EnableSwagger2
@CompileStatic
class JetpackerApplication {
	static void main(String[] args) {
		SpringApplication.run JetpackerApplication, args
	}
}
