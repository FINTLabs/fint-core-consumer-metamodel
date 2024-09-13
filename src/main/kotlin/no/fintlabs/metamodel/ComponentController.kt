package no.fintlabs.metamodel

import no.fintlabs.metamodel.metadata.MetadataCache
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/components")
class ComponentController(val metadataCache: MetadataCache) {

    @GetMapping
    fun getComponents(): Set<String> {
        val uniqueDomainPackages = metadataCache.domainPackageCache.keys
            .map { "${it.first}.${it.second}" }
            .toSet()

        return uniqueDomainPackages
    }

}