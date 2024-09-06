package no.fintlabs

import org.springframework.stereotype.Service

@Service
class MetadataCache {

    val metamodels: MutableList<Metadata> = mutableListOf()

    fun add(metaData: Metadata) {
        metamodels.add(metaData)
    }

}