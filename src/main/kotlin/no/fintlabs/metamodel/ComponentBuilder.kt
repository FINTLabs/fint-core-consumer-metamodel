package no.fintlabs.metamodel

import no.fintlabs.metamodel.model.Component
import no.fintlabs.metamodel.model.Resource
import no.fintlabs.metamodel.model.createComponent
import no.fintlabs.metamodel.model.createResource
import no.novari.fint.model.FintModelObject
import no.novari.fint.model.FintRelation
import no.novari.fint.model.resource.FintResource
import org.springframework.stereotype.Service

data class ResourceContext(
    val fintModelObject: FintModelObject,
    val resourceClass: Class<out FintResource>
)

@Service
class ComponentBuilder(
    private val reflectionService: ReflectionService,
) {

    fun buildComponents(): List<Component> =
        createBaseResourceContexts()
            .groupBy { it.fintModelObject.javaClass.packageName }
            .map { (packageName, resourceContexts) ->
                val component = packageName.createComponent()
                val resources = resourceContexts.createResources(component)
                val resourcesToScan = resources.toList() // Snapshot to avoid ConcurrentModificationException
                val visitedClasses = mutableSetOf<String>()

                resourcesToScan.forEach { resource ->
                    resource.crawlCommonResources(component, resources, visitedClasses)
                }

                component.setResources(resources)

                component
            }

    /**
     * Creates a list of resource contexts for non-common resources.
     */
    private fun createBaseResourceContexts(): List<ResourceContext> =
        reflectionService.getFintModelObjects()
            .filter { !it.isCommonResource() }
            .filter { it.isNotKodeverkIso() }
            .map { it.toResourceContext() }

    /**
     * Recursively crawls all common resources and adds them to the list of resources.
     */
    private fun Resource.crawlCommonResources(
        component: Component,
        allResources: MutableList<Resource>,
        visitedClasses: MutableSet<String>,
    ): Unit = this.relations
        .filter { it.isCommonResource() }
        .filter { visitedClasses.add(it.packageName) }
        .map { relation -> relation.toResourceContext() }
        .createResources(component)
        .forEach {
            allResources.add(it)
            it.crawlCommonResources(component, allResources, visitedClasses)
        }

    // packageName is actually a className
    private fun FintRelation.toResourceContext() =
        reflectionService.getFintModelObject(this.packageName)
            ?.toResourceContext()
            ?: error("Couldn't find meta model: ${this.packageName}")

    private fun List<ResourceContext>.createResources(component: Component) =
        this.map { createResource(it.fintModelObject, it.resourceClass, component) }.toMutableList()

    /**
     * Returns true if the class name only has 5 parts, indicating it's a common resource.
     * For example, "no.novari.fint.model.felles.Person" is a common resource.
     */
    private fun FintModelObject.isCommonResource() =
        this.javaClass.name.split(".").size == 6

    // packageName is actually a className
    private fun FintRelation.isCommonResource() =
        this.packageName.split(".").size == 6

    // I have no idea what felles kodeverk iso is used for, there are no consumers of it. So I skip it.
    private fun FintModelObject.isNotKodeverkIso() =
        this.javaClass.packageName != "no.fint.model.felles.kodeverk.iso"

    private fun FintModelObject.toResourceContext() =
        ResourceContext(this, this.resourceClass())

    private fun FintModelObject.resourceClass(): Class<out FintResource> =
        reflectionService.getResourceClass(this.javaClass.name)
            ?: error("Couldn't find resource: ${this.javaClass.name}")

}