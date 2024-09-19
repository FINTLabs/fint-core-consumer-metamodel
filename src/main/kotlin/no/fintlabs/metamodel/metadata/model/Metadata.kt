package no.fintlabs.metamodel.metadata.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Metadata(
    val domainName: String,
    val packageName: String?,
    val resourceName: String,
    val fields: List<String>,
    val idFields: Set<String>,
    val writeable: Boolean,
    val relations: List<FintRelationMetadata>
)
