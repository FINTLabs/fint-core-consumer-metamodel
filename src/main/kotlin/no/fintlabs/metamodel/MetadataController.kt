package no.fintlabs.metamodel

import no.fintlabs.metamodel.model.Metadata
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class MetadataController(val metadataCache: MetadataCache) {

    @GetMapping
    fun getAllMetadata(): List<Metadata> = metadataCache.metamodels

    @GetMapping("/{domain}")
    fun getMetadataByDomain(@PathVariable domain: String): List<Metadata> =
        metadataCache.getByDomain(domain)
            ?: throw DomainNotFoundException(domain)

    @GetMapping("/{domain}/{packageName}")
    fun getMetadataByDomainAndPackage(
        @PathVariable domain: String,
        @PathVariable packageName: String
    ): List<Metadata> =
        metadataCache.getByDomainAndPackage(domain, packageName)
            ?: throw PackageNotFoundException(domain, packageName)

    @GetMapping("/{domain}/{packageName}/{resourceName}")
    fun getMetadataByDomainPackageAndResource(
        @PathVariable domain: String,
        @PathVariable packageName: String,
        @PathVariable resourceName: String
    ): Metadata =
        metadataCache.getByDomainPackageAndResource(domain, packageName, resourceName)
            ?: throw ResourceNotFoundException(domain, packageName, resourceName)

}

@ResponseStatus(HttpStatus.NOT_FOUND)
class DomainNotFoundException(domain: String) : RuntimeException("Domain '$domain' not found")

@ResponseStatus(HttpStatus.NOT_FOUND)
class PackageNotFoundException(domain: String, packageName: String) :
    RuntimeException("Package '$packageName' not found in domain '$domain'")

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException(domain: String, packageName: String, resourceName: String) :
    RuntimeException("Resource '$resourceName' not found in domain '$domain' and package '$packageName'")
