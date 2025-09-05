package no.fintlabs.metamodel.model.builder

import no.fintlabs.metamodel.config.MetamodelProperties
import no.fintlabs.metamodel.model.Component
import org.springframework.stereotype.Service

@Service
class ComponentBuilder(
    private val resourceBuilder: ResourceBuilder,
    private val metamodelProperties: MetamodelProperties
) {

    fun createComponents() =
        resourceBuilder.createComponentToResourcesPair().map { (componentName, resources) ->
            Component(
                component = componentName,
                format = metamodelProperties.format,
                resources = resources
            )
        }

}