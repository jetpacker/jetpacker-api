package jetpacker.api

import groovy.transform.CompileStatic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@CompileStatic
class JetpackerApplication {
	static void main(String[] args) {
		SpringApplication.run JetpackerApplication, args
	}
}
