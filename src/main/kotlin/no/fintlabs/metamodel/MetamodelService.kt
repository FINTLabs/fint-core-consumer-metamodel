package no.fintlabs.metamodel

import no.fintlabs.metamodel.metadata.MetadataCache
import org.springframework.stereotype.Service

@Service
class MetamodelService(
    private val metadataCache: MetadataCache
) {

    fun getComponents(): Set<String> =
        metadataCache.domainPackageCache.keys
            .map { "${it.first}.${it.second}" }
            .toSet()

    fun getMetadata() =
        metadataCache.metamodels

    fun getMetadata(domain: String) =
        metadataCache.getByDomain(domain)

    fun getMetadata(domain: String, `package`: String) =
        metadataCache.getByDomainAndPackage(domain, `package`)

    fun getMetadata(domain: String, `package`: String, resource: String) =
        metadataCache.getByDomainPackageAndResource(domain, `package`, resource)

}