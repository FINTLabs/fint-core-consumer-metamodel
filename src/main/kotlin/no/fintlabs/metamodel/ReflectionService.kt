package no.fintlabs.metamodel

import jakarta.annotation.PostConstruct
import no.fint.model.FintModelObject
import no.fintlabs.metamodel.model.FintRelationMetadata
import no.fintlabs.metamodel.model.Metadata
import org.reflections.Reflections
import org.springframework.stereotype.Service

@Service
class ReflectionService(
    val metadataCache: MetadataCache,
) {

    @PostConstruct
    fun fillCache() {
        Reflections("no.fint.model")
            .getSubTypesOf(FintModelObject::class.java)
            .forEach { clazz ->
                val fintModelObject = newInstanceOfFintModelObject(clazz)
                val relationsMetadata = fintModelObject.relations.map { relation ->
                    FintRelationMetadata(
                        relation.name.lowercase(),
                        relation.multiplicity,
                        relation.packageName
                    )
                }

                metadataCache.add(
                    Metadata(
                        clazz.packageName.split(".")[3],
                        clazz.packageName.split(".").getOrNull(4),
                        clazz.simpleName.lowercase(),
                        fintModelObject.identifikators.keys,
                        fintModelObject.isWriteable,
                        relationsMetadata
                    )
                )
            }
    }

    fun newInstanceOfFintModelObject(clazz: Class<out FintModelObject>): FintModelObject {
        try {
            return clazz.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            throw e
        }
    }

}
