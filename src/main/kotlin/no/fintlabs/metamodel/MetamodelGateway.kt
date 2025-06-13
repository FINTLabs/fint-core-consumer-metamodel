package no.fintlabs.metamodel

import no.fintlabs.metamodel.metadata.MetadataCache
import no.fintlabs.metamodel.metadata.model.Resource
import org.springframework.stereotype.Repository

@Repository
class MetamodelGateway(
    private val metadataCache: MetadataCache
) {

    fun getComponents() = metadataCache.getComponents()

    fun getComponent(component: String): String? =
        getComponents().firstOrNull { it.equals(component, ignoreCase = true) }

    fun getFieldNames(component: String, resource: String): Set<String> =
        getResources(component)
            .first { it.name.equals(resource, true) }
            .fields

    fun getRelationNames(component: String, resource: String): Set<String> =
        getResources(component)
            .first { it.name.equals(resource, true) }
            .relations
            .map { it.name }
            .toSet()

    private fun getResources(component: String): List<Resource> =
        component.split("-")
            .let { metadataCache.getResources(it[0].lowercase(), it[1].lowercase()) }
            .toList()

    fun getResourceNames(component: String): Set<String> =
        getResources(component)
            .map { it.name }
            .toSet()

}
