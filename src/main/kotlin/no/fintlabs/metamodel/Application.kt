package no.fintlabs.metamodel

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FintCoreConsumerMetamodelApplication

fun main(args: Array<String>) {
	runApplication<FintCoreConsumerMetamodelApplication>(*args)
}
