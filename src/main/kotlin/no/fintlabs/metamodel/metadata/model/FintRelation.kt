package no.fintlabs.metamodel.metadata.model

import no.fint.model.FintMultiplicity

data class FintRelation(
    val name: String,
    val multiplicity: FintMultiplicity,
    val classPackageName: String
)
