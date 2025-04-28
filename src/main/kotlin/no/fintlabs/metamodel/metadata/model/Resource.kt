package no.fintlabs.metamodel.metadata.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.fint.model.FintModelObject
import no.fint.model.FintRelation
import java.lang.reflect.Modifier

data class Resource(
    val name: String,
    val packageName: String,
    val isCommon: Boolean,
    val writeable: Boolean,
    val fields: Set<String>,
    val idFields: Set<String>,
    val relations: List<FintRelation>
) {
    companion object {
        fun from(fintModelObject: FintModelObject) =
            Resource(
                name = fintModelObject.javaClass.simpleName,
                packageName = fintModelObject.javaClass.packageName,
                isCommon = isCommon(fintModelObject.javaClass.packageName),
                writeable = fintModelObject.isWriteable,
                fields = getFields(fintModelObject.javaClass),
                idFields = fintModelObject.identifikators.keys,
                relations = fintModelObject.relations
            )

        private fun isCommon(packageName: String) =
            packageName.split(".").size == 4

        private fun getFields(clazz: Class<*>): Set<String> =
            generateSequence(clazz) { it.superclass }
                .takeWhile { it != Any::class.java }
                .flatMap { it.declaredFields.asSequence() }
                .filter { field ->
                    !field.isAnnotationPresent(JsonIgnore::class.java)
                            && !Modifier.isStatic(field.modifiers)
                            && !field.isSynthetic
                }
                .map { it.name }
                .toSet()
    }
}