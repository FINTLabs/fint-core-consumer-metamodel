package no.fintlabs.metamodel.cache

import no.fintlabs.metamodel.model.Component
import no.fintlabs.metamodel.model.Resource
import org.springframework.stereotype.Service

@Service
class MetamodelCache(
    metamodelCacheInitializer: ResourceBuilder
) {

    private val cache: MutableMap<Component, MutableSet<Resource>> by lazy {
        metamodelCacheInitializer
            .buildResourcePairs()
            .fold(mutableMapOf()) { acc, (main, related) ->
                acc.apply { addToBucket(main.component, main, related) }
            }
    }

    fun getComponents(): Set<Component> = cache.keys

    fun getResources(domainName: String, packageName: String): Set<Resource> =
        cache.getOrDefault(Component(domainName, packageName), mutableSetOf())

    fun componentExists(domainName: String, packageName: String) =
        cache.containsKey(Component(domainName, packageName))

    private fun MutableMap<Component, MutableSet<Resource>>.addToBucket(
        component: Component,
        main: Resource,
        related: List<Resource>
    ) {
        getOrPut(component) { mutableSetOf() }.apply {
            add(main)
            addAll(related)
        }
    }
}