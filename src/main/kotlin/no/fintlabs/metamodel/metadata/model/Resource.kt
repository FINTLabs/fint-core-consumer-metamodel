package no.fintlabs.metamodel.metadata.model

import no.fint.model.FintRelation
import no.fint.model.resource.FintResource

data class Resource(
    val name: String,
    val packageName: String,
    val component: Component,
    val resourceType: Class<out FintResource>,
    val isCommon: Boolean,
    val writeable: Boolean,
    val fields: Set<String>,
    val idFields: Set<String>,
    val relations: List<FintRelation>
)