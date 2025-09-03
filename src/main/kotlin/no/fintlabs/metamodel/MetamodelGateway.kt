package no.fintlabs.metamodel

import no.fint.model.FintRelation
import no.fintlabs.metamodel.cache.MetamodelCache
import no.fintlabs.metamodel.config.MetamodelProperties
import no.fintlabs.metamodel.model.Resource
import org.springframework.stereotype.Repository

@Repository
class MetamodelGateway(
    private val metamodelProps: MetamodelProperties,
    private val metamodelCache: MetamodelCache
) {

    fun getComponents() = metamodelCache.getComponents()

    fun getComponentNames() = metamodelCache.getComponents()
        .map { "${it.domainName}${metamodelProps.format}${it.packageName}" }

    fun getResources(component: String): Set<Resource> =
        component.getDomainAndPackage()
            ?.let { (domain, pkg) -> metamodelCache.getResources(domain, pkg) }
            ?: emptySet()

    fun getResources(domainName: String, packageName: String): Set<Resource> =
        metamodelCache.getResources(domainName, packageName)

    fun getResource(component: String, resource: String): Resource? =
        getResources(component).firstOrNull { it.name.equals(resource, ignoreCase = true) }

    fun getResource(domainName: String, packageName: String, resource: String): Resource? =
        getResources(domainName, packageName).firstOrNull { it.name.equals(resource, ignoreCase = true) }

    fun getResourceNames(component: String): Set<String> =
        getResources(component).map { it.name }.toSet()

    fun getResourceNames(domainName: String, packageName: String): Set<String> =
        getResources(domainName, packageName).map { it.name }.toSet()

    fun getFieldNames(component: String, resource: String): Set<String> =
        getResource(component, resource)?.fields ?: emptySet()

    fun getFieldNames(domainName: String, packageName: String, resource: String): Set<String> =
        getResource(domainName, packageName, resource)?.fields ?: emptySet()

    fun getIdNames(component: String, resource: String): Set<String> =
        getResource(component, resource)?.idFields ?: emptySet()

    fun getIdNames(domainName: String, packageName: String, resource: String): Set<String> =
        getResource(domainName, packageName, resource)?.idFields ?: emptySet()

    fun getRelations(component: String, resource: String): List<FintRelation> =
        getResource(component, resource)?.relations ?: emptyList()

    fun getRelations(domainName: String, packageName: String, resource: String): List<FintRelation> =
        getResource(domainName, packageName, resource)?.relations ?: emptyList()

    fun getRelationNames(component: String, resource: String): Set<String> =
        getRelations(component, resource).map { it.name }.toSet()

    fun getRelationNames(domainName: String, packageName: String, resource: String): Set<String> =
        getRelations(domainName, packageName, resource).map { it.name }.toSet()

    private fun String.getDomainAndPackage(): Pair<String, String>? =
        this.split(metamodelProps.format)
            .takeIf { it.size >= 2 }
            ?.let { (domain, pkg) -> domain to pkg }

}
