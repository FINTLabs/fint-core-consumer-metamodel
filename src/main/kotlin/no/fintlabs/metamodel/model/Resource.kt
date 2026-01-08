package no.fintlabs.metamodel.model

import no.novari.fint.model.FintRelation
import no.novari.fint.model.resource.FintResource

class Resource(
    name: String,
    val packageName: String,
    val resourceType: Class<out FintResource>,
    val isCommon: Boolean,
    val writeable: Boolean,
    val fields: Set<String>,
    val idFields: Set<String>,
    val relations: List<FintRelation>
) {
    val name: String = name.lowercase()
}