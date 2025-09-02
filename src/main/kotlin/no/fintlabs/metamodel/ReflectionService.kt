package no.fintlabs.metamodel

import no.fint.model.FintModelObject
import no.fint.model.resource.FintResource
import org.reflections.Reflections
import org.springframework.stereotype.Service

@Service
class ReflectionService(
    reflections: Reflections = Reflections("no.fint.model")
) {

    val fintModelObjects: Map<String, FintModelObject> = initializeFintModelObjects(reflections)
    val fintResourceObjects: Map<String, Class<out FintResource>> = initializeFintResources(reflections)

    private fun initializeFintResources(reflections: Reflections) =
        reflections.getSubTypesOf(FintResource::class.java)
            .associateBy { it.packageName.replace(".resource", "") }

    private fun initializeFintModelObjects(reflections: Reflections) =
        reflections.getSubTypesOf(FintModelObject::class.java)
            .map { initializeFintModelObject(it) }
            .associateBy { it.javaClass.packageName }

    fun initializeFintModelObject(clazz: Class<out FintModelObject>): FintModelObject =
        runCatching { clazz.getDeclaredConstructor().newInstance() }
            .getOrElse { error("Couldn't initialize FintModelObject") }

}
