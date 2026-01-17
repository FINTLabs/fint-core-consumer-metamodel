package no.fintlabs.metamodel.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.fint.model.FintModelObject
import no.fint.model.FintRelation
import no.fint.model.resource.FintResource
import java.lang.reflect.Modifier


class Resource(
    name: String,
    val component: Component,
    val className: String,
    val resourceClass: Class<out FintResource>,
    val isCommon: Boolean,
    val writeable: Boolean,
    val fields: Set<String>,
    val idFields: Set<String>,
    val relations: List<FintRelation>,
    val relationUri: Map<String, String>
) {
    val name: String = name.lowercase()
}

fun createResource(
    fintModelObject: FintModelObject,
    resourceClass: Class<out FintResource>,
    component: Component
): Resource =
    fintModelObject.javaClass.let { javaClass ->
        Resource(
            name = javaClass.simpleName,
            component = component,
            className = javaClass.name,
            resourceClass = resourceClass,
            isCommon = javaClass.packageName.split(".").size == 4,
            writeable = fintModelObject.isWriteable,
            fields = javaClass.getNonIgnoredFields(),
            idFields = fintModelObject.identifikators.keys,
            relations = fintModelObject.relations,
            relationUri = fintModelObject.relations.associate { it.name to it.toRelationUri(component) }
        )
    }

fun FintRelation.toRelationUri(component: Component): String {
    val parts = this.packageName.split(".")

    val path = if (parts.size == 5) {
        "${component.domainName}/${component.packageName}/${parts.last()}"
    } else {
        parts.takeLast(3).joinToString("/")
    }

    return path.lowercase()
}

private fun Class<*>.getNonIgnoredFields() =
    generateSequence(this) { it.superclass }
        .takeWhile { it != Any::class.java }
        .flatMap { it.declaredFields.asSequence() }
        .filter { field ->
            !field.isAnnotationPresent(JsonIgnore::class.java)
                    && !Modifier.isStatic(field.modifiers)
                    && !field.isSynthetic
        }
        .map { it.name }
        .toSet()
