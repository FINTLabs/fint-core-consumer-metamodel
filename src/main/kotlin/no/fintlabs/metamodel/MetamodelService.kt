package no.fintlabs.metamodel

import no.fintlabs.metamodel.model.Component
import no.fintlabs.metamodel.model.Resource
import org.springframework.stereotype.Service

@Service
class MetamodelService(
    componentBuilder: ComponentBuilder
) {

    private val componentCache: Map<Pair<String, String>, Component> by lazy {
        componentBuilder.buildComponents().associateBy { it.domainName to it.packageName }
    }

    fun getComponents(): List<Component> = componentCache.values.toList()

    fun getComponent(domainName: String, packageName: String): Component? =
        componentCache[domainName to packageName]

    fun getResources(): List<Resource> = componentCache.values.flatMap { it.resources }

    fun getResources(domainName: String, packageName: String): List<Resource> =
        getComponent(domainName, packageName)?.resources ?: emptyList()

    fun getResource(domainName: String, packageName: String, resourceName: String): Resource? =
        getComponent(domainName, packageName)?.resources?.findResourceByName(resourceName)

    private fun List<Resource>.findResourceByName(name: String): Resource? =
        firstOrNull { it.name.equals(name, ignoreCase = true) }

}