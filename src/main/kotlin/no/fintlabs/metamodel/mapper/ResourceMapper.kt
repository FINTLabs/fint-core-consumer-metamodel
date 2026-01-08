package no.fintlabs.metamodel.mapper

import com.fasterxml.jackson.annotation.JsonIgnore
import no.novari.fint.model.FintModelObject
import no.novari.fint.model.resource.FintResource
import no.fintlabs.metamodel.model.Resource
import org.springframework.stereotype.Service
import java.lang.reflect.Modifier

@Service
class ResourceMapper {

    fun createResource(fintModelObject: FintModelObject, resourceType: Class<out FintResource>) =
        Resource(
            name = fintModelObject.javaClass.simpleName,
            packageName = fintModelObject.javaClass.name,
            resourceType = resourceType,
            isCommon = isCommon(fintModelObject.javaClass.packageName),
            writeable = fintModelObject.isWriteable,
            fields = getFields(fintModelObject.javaClass),
            idFields = fintModelObject.identifikators.keys,
            relations = fintModelObject.relations
        )

    private fun isCommon(packageName: String) =
        packageName.split(".").size == 5

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