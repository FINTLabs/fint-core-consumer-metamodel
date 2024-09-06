package no.fintlabs

import no.fint.model.FintMultiplicity

data class FintRelationMetadata(
    val name: String,
    val multiplicity: FintMultiplicity,
    val classPackageName: String
)
