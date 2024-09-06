package no.fintlabs.metamodel

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MetadataController(val metadataCache: MetadataCache) {

    @GetMapping
    fun getMetadata() = metadataCache.metamodels

}