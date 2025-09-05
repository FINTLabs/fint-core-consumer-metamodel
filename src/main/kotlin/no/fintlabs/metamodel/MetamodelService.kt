package no.fintlabs.metamodel

import no.fintlabs.metamodel.model.Component
import no.fintlabs.metamodel.model.Resource
import no.fintlabs.metamodel.model.builder.ComponentBuilder
import org.springframework.stereotype.Service

@Service
class MetamodelService(
    componentBuilder: ComponentBuilder
) {

    private val cache: List<Component> by lazy {
        componentBuilder.createComponents()
    }

    fun getComponents(): List<Component> = cache

    fun getComponent(component: String): Component? =
        cache.firstOrNull { it.name.equals(component, ignoreCase = true) }

    fun getComponent(domainName: String, packageName: String): Component? =
        cache.firstOrNull { it.nameEquals(domainName, packageName) }

    fun getResources(): List<Resource> = cache.flatMap { it.resources }

    fun getResources(component: String): List<Resource> =
        getComponent(component)?.resources ?: emptyList()

    fun getResources(domainName: String, packageName: String): List<Resource> =
        getComponent(domainName, packageName)?.resources ?: emptyList()

    fun getResource(component: String, resource: String): Resource? =
        getComponent(component)?.resources?.findResourceByName(resource)

    fun getResource(domainName: String, packageName: String, resource: String): Resource? =
        getComponent(domainName, packageName)?.resources?.findResourceByName(resource)

    private fun List<Resource>.findResourceByName(resource: String): Resource? =
        firstOrNull { it.name.equals(resource, ignoreCase = true) }

}