package no.fintlabs.metamodel

import no.fintlabs.metamodel.model.Metadata
import org.springframework.stereotype.Service

@Service
class MetadataCache {

    val metamodels: MutableList<Metadata> = mutableListOf()

    fun add(metaData: Metadata) {
        metamodels.add(metaData)
    }

}