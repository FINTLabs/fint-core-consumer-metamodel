package no.fintlabs.metamodel.model.builder

import no.novari.fint.model.FintModelObject
import no.novari.fint.model.resource.FintResource
import no.fintlabs.metamodel.ReflectionService
import no.fintlabs.metamodel.mapper.ResourceMapper
import no.fintlabs.metamodel.model.Resource
import org.springframework.stereotype.Service

@Service
class ResourceBuilder(
    private val resourceMapper: ResourceMapper,
    private val reflectionService: ReflectionService
) {

    fun createComponentToResourcesPair(): Map<String, List<Resource>> =
        createResources().map { createResourceToCommonResourcePair(it) }
            .groupBy { (resource, _) -> resource.getComponent() }
            .mapValues { (_, buckets) ->
                buckets
                    .flatMap { (resource, commonResources) -> listOf(resource) + commonResources }
                    .distinctBy { it.name }
            }

    private fun createResources(): List<Resource> =
        reflectionService.fintModelObjects.values
            .filter { !isCommon(it.javaClass.packageName) }
            .map(::createResource)

    private fun Resource.getComponent(): String =
        getDomainAndPackage(this.packageName)
            .let { (domainName, packageName) -> "$domainName-$packageName" }

    private fun createResourceToCommonResourcePair(resource: Resource): Pair<Resource, List<Resource>> =
        resource to createCommonResources(resource)

    private fun createCommonResources(resource: Resource): List<Resource> =
        getAllCommonResources(resource).map(::createResource)

    private fun getAllCommonResources(resource: Resource) =
        resource.relations
            .mapNotNull { reflectionService.fintModelObjects[it.packageName] }
            .filter { isCommon(it.javaClass.packageName) }

    private fun createResource(fintModelObject: FintModelObject): Resource =
        resourceMapper.createResource(
            fintModelObject,
            getResourceType(fintModelObject.javaClass.name)
        )

    private fun getResourceType(packageName: String): Class<out FintResource> =
        reflectionService.fintResourceObjects[packageName]
            ?: error("Couldn't find resource: $packageName")

    private fun getDomainAndPackage(clazzPackage: String): Pair<String, String> =
        clazzPackage.split(".").let { it[4] to it[5] }

    private fun isCommon(packageName: String): Boolean =
        packageName.startsWith("no.novari.fint.model.felles")

}