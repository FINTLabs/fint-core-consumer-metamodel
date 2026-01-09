package no.fintlabs.metamodel.model

import com.fasterxml.jackson.annotation.JsonIgnore
import no.novari.fint.model.FintModelObject
import no.novari.fint.model.FintRelation
import no.novari.fint.model.resource.FintResource
import java.lang.reflect.Modifier
import kotlin.jvm.java


class Resource(
    name: String,
    val component: Component,
    val className: String,
    val resourceClass: Class<out FintResource>,
    val isCommon: Boolean,
    val writeable: Boolean,
    val fields: Set<String>,
    val idFields: Set<String>,
    val relations: List<FintRelation>
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
            fields = getFields(javaClass),
            idFields = fintModelObject.identifikators.keys,
            relations = fintModelObject.relations
        )
    }

private fun getFields(clazz: Class<*>) =
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
