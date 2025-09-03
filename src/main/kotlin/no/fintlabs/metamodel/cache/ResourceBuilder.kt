package no.fintlabs.metamodel.cache

import no.fint.model.FintModelObject
import no.fint.model.resource.FintResource
import no.fintlabs.metamodel.ReflectionService
import no.fintlabs.metamodel.mapper.ResourceMapper
import no.fintlabs.metamodel.model.Resource
import org.springframework.stereotype.Service

@Service
class ResourceBuilder(
    private val resourceMapper: ResourceMapper,
    private val reflectionService: ReflectionService
) {

    fun buildResourcePairs(): List<Pair<Resource, List<Resource>>> =
        createResources().map { resource ->
            resource to getAllCommonResources(resource).map(::createResource)
        }

    private fun createResources(): List<Resource> =
        reflectionService.fintModelObjects.values
            .filter { !isCommon(it.javaClass.packageName) }
            .map(::createResource)

    private fun createResource(fintModelObject: FintModelObject): Resource =
        resourceMapper.createResource(
            fintModelObject,
            getResourceType(fintModelObject.javaClass.name)
        )

    private fun getResourceType(packageName: String): Class<out FintResource> =
        reflectionService.fintResourceObjects[packageName]
            ?: error("Couldn't find resource: $packageName")

    private fun getAllCommonResources(resource: Resource) =
        resource.relations
            .mapNotNull { reflectionService.fintModelObjects[it.packageName] }
            .filter { isCommon(it.javaClass.packageName) }


    private fun isCommon(packageName: String): Boolean =
        packageName.startsWith("no.fint.model.felles")

}