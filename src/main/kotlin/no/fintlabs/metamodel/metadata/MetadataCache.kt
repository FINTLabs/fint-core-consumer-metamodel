package no.fintlabs.metamodel.metadata

import no.fintlabs.metamodel.metadata.model.Resource
import org.springframework.stereotype.Service

@Service
class MetadataCache {

    private val cache: MutableMap<String, MutableSet<Resource>> = mutableMapOf()

    fun getResources(domainName: String, packageName: String): Collection<Resource> =
        cache.getOrDefault(formatComponentName(domainName, packageName), mutableSetOf())

    fun componentExists(domainName: String, packageName: String) =
        cache.containsKey(formatComponentName(domainName, packageName))

    private fun getOrSetDefault(domainName: String, packageName: String) =
        cache.getOrPut(formatComponentName(domainName, packageName)) { mutableSetOf() }

    fun addResource(domainName: String, packageName: String, resource: Resource) =
        getOrSetDefault(domainName, packageName).add(resource)

    private fun formatComponentName(domainName: String, packageName: String) = "$domainName.$packageName"

}
