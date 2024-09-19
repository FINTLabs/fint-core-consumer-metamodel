package no.fintlabs.metamodel

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.annotation.PostConstruct
import no.fint.model.FintModelObject
import no.fintlabs.metamodel.metadata.MetadataCache
import no.fintlabs.metamodel.metadata.model.FintRelationMetadata
import no.fintlabs.metamodel.metadata.model.Metadata
import org.reflections.Reflections
import org.springframework.stereotype.Service
import java.lang.reflect.Field
import java.util.stream.Collectors
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

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
                getFieldNames(clazz),
                fintModelObject.identifikators.keys,
                fintModelObject.isWriteable,
                relationsMetadatas
            )
        )
    }

    private fun createClazzMetadata(
        parentClazz: Class<out FintModelObject>,
        relationClazz: Class<out FintModelObject>,
        existingRelationPackages: MutableSet<String>
    ) {
        if (!existingRelationPackages.add(relationClazz.name)) return
        val fintModelObject = newInstanceOfFintModelObject(relationClazz)
        val relationsMetadatas = createRelationMetadatas(fintModelObject)

        metadataCache.add(
            Metadata(
                parentClazz.packageName.split(".")[3],
                parentClazz.packageName.split(".")[4],
                relationClazz.simpleName.lowercase(),
                getFieldNames(relationClazz),
                fintModelObject.identifikators.keys,
                fintModelObject.isWriteable,
                relationsMetadatas
            )
        )

    }

    private fun getFieldNames(clazz: Class<out FintModelObject>): List<String> {
        val fieldNames = clazz.getAllFieldsRecursively()
            .filter { !it.isAnnotationPresent(JsonIgnore::class.java) }
            .map { it.name }

        val propertyNames = clazz.getAllKotlinPropertiesRecursively()

        return fieldNames + propertyNames
    }

    private fun Class<*>.getAllFieldsRecursively(): List<Field> =
        this.declaredFields.toList() + (this.superclass?.takeIf { it != Any::class.java }?.getAllFieldsRecursively() ?: emptyList())

    private fun Class<*>.getAllKotlinPropertiesRecursively(): List<String> =
        this.kotlin.declaredMemberProperties
            .filter { it.findAnnotation<JsonIgnore>() == null }
            .map { it.name } +
                (this.superclass?.takeIf { it != Any::class.java }?.getAllKotlinPropertiesRecursively() ?: emptyList())

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
