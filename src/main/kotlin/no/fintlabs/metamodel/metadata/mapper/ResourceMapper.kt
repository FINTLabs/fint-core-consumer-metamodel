package no.fintlabs.metamodel.metadata.mapper

import com.fasterxml.jackson.annotation.JsonIgnore
import no.fint.model.FintModelObject
import no.fint.model.resource.FintResource
import no.fintlabs.metamodel.metadata.model.Component
import no.fintlabs.metamodel.metadata.model.Resource
import org.springframework.stereotype.Service
import java.lang.reflect.Modifier

@Service
class ResourceMapper {

    fun createResource(fintModelObject: FintModelObject, resourceType: Class<out FintResource>) =
        Resource(
            name = fintModelObject.javaClass.simpleName,
            component = createComponent(fintModelObject),
            packageName = fintModelObject.javaClass.packageName,
            resourceType = resourceType,
            isCommon = isCommon(fintModelObject.javaClass.packageName),
            writeable = fintModelObject.isWriteable,
            fields = getFields(fintModelObject.javaClass),
            idFields = fintModelObject.identifikators.keys,
            relations = fintModelObject.relations
        )

    private fun createComponent(fintModelObject: FintModelObject) =
        getDomainAndPackage(fintModelObject.javaClass.packageName)
            .let { (domainName, packageName) -> Component(domainName, packageName) }

    private fun getDomainAndPackage(clazzPackage: String): Pair<String, String> =
        clazzPackage.split(".").let { it[3] to it[4] }

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