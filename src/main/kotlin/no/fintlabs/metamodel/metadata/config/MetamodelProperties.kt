package no.fintlabs.metamodel.metadata.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("fint.metamodel")
class MetamodelProperties {

    var format: String = "."

}