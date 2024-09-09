package no.fintlabs.metamodel

import jakarta.annotation.PostConstruct
import no.fint.model.FintModelObject
import no.fintlabs.metamodel.model.FintRelationMetadata
import no.fintlabs.metamodel.model.Metadata
import org.reflections.Reflections
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class ReflectionService(
    val metadataCache: MetadataCache,
) {

    val clazzMap: Map<String, Class<out FintModelObject>> = createClazzMap()

    @PostConstruct
    fun fillCache() {
        clazzMap.forEach { createClazzMetadata(it.value) }
    }

    private fun createClazzMetadata(clazz: Class<out FintModelObject>) {
        val packageName = clazz.packageName
        if (packageName.split(".").size == 4) return

        val fintModelObject = newInstanceOfFintModelObject(clazz)
        val relationsMetadatas = createRelationMetadatas(fintModelObject)
        val existingRelationPackages: MutableSet<String> = mutableSetOf()
        relationsMetadatas.forEach {
            if (it.classPackageName.split(".").size == 5)
                clazzMap[it.classPackageName.lowercase()]?.let { relationClazz ->
                    createClazzMetadata(clazz, relationClazz, existingRelationPackages)
                }
        }

        metadataCache.add(
            Metadata(
                clazz.packageName.split(".")[3],
                clazz.packageName.split(".")[4],
                clazz.simpleName.lowercase(),
                fintModelObject.identifikators.keys,
                fintModelObject.isWriteable,
                relationsMetadatas
            )
        )
    }

    private fun createClazzMetadata(
        clazz: Class<out FintModelObject>,
        relationClazz: Class<out FintModelObject>,
        existingRelationPackages: MutableSet<String>
    ) {
        if (!existingRelationPackages.add(relationClazz.name)) return
        val fintModelObject = newInstanceOfFintModelObject(relationClazz)
        val relationsMetadatas = createRelationMetadatas(fintModelObject)
        relationsMetadatas.forEach {
            if (it.classPackageName.split(".").size == 4)
                clazzMap[it.classPackageName]?.let { relationClazz ->
                    createClazzMetadata(clazz, relationClazz, existingRelationPackages)
                }
        }

        metadataCache.add(
            Metadata(
                clazz.packageName.split(".")[3],
                clazz.packageName.split(".")[4],
                relationClazz.simpleName.lowercase(),
                fintModelObject.identifikators.keys,
                fintModelObject.isWriteable,
                relationsMetadatas
            )
        )

    }

    private fun createClazzMap(): Map<String, Class<out FintModelObject>> =
        Reflections("no.fint.model")
            .getSubTypesOf(FintModelObject::class.java).stream()
            .collect(Collectors.toMap(
                { clazz -> clazz.getName().lowercase() },
                { clazz -> clazz }
            ))

    private fun createRelationMetadatas(fintModelObject: FintModelObject): List<FintRelationMetadata> {
        return fintModelObject.relations.map { relation ->
            FintRelationMetadata(
                relation.name.lowercase(),
                relation.multiplicity,
                relation.packageName
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
