package no.fintlabs.metamodel.metadata.model

data class Resource(
    val name: String,
    val writeable: Boolean,
    val fields: Set<String>,
    val idFields: Set<String>,
    val relations: List<FintRelation>
)