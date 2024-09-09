package no.fintlabs.metamodel

import no.fintlabs.metamodel.model.Metadata
import org.springframework.stereotype.Service

@Service
class MetadataCache {

    private val domainCache: MutableMap<String, List<Metadata>> = mutableMapOf()
    private val domainPackageCache: MutableMap<Pair<String, String>, List<Metadata>> = mutableMapOf()
    private val resourceCache: MutableMap<Triple<String, String, String>, Metadata> = mutableMapOf()

    val metamodels: MutableList<Metadata> = mutableListOf()

    fun add(metaData: Metadata) {
        metamodels.add(metaData)

        val domain = metaData.domainName.lowercase()
        domainCache[domain] = domainCache.getOrDefault(domain, mutableListOf()) + metaData

        metaData.packageName?.let { packageName ->
            val domainPackageKey = domain to packageName.lowercase()
            domainPackageCache[domainPackageKey] =
                domainPackageCache.getOrDefault(domainPackageKey, mutableListOf()) + metaData
        }

        val resourceKey = Triple(domain, metaData.packageName?.lowercase() ?: "", metaData.resourceName.lowercase())
        resourceCache[resourceKey] = metaData
    }

    fun getByDomain(domain: String): List<Metadata>? {
        return domainCache[domain.lowercase()]
    }

    fun getByDomainAndPackage(domain: String, packageName: String): List<Metadata>? {
        return domainPackageCache[domain.lowercase() to packageName.lowercase()]
    }

    fun getByDomainPackageAndResource(domain: String, packageName: String, resourceName: String): Metadata? {
        return resourceCache[Triple(domain.lowercase(), packageName.lowercase(), resourceName.lowercase())]
    }

}
