package no.fintlabs.metamodel

import no.novari.fint.model.FintModelObject
import no.novari.fint.model.resource.FintResource
import org.reflections.Reflections
import org.springframework.stereotype.Service

@Service
class ReflectionService(
    reflections: Reflections = Reflections("no.novari.fint.model")
) {

    private val fintModelObjects: Map<String, FintModelObject> = initializeFintModelObjects(reflections)
    private val fintResourceObjects: Map<String, Class<out FintResource>> = initializeFintResources(reflections)

    fun getFintModelObjects() = fintModelObjects.values
    fun getFintModelObject(className: String) = fintModelObjects[className]
    fun getResourceClass(className: String) = fintResourceObjects[className]

    private fun initializeFintResources(reflections: Reflections) =
        reflections.getSubTypesOf(FintResource::class.java)
            .associateBy {
                it.name.replace(".resource", "")
                    .replace("Resource", "")
            }

    private fun initializeFintModelObjects(reflections: Reflections) =
        reflections.getSubTypesOf(FintModelObject::class.java)
            .map { initializeFintModelObject(it) }
            .associateBy { it.javaClass.name }

    fun initializeFintModelObject(clazz: Class<out FintModelObject>): FintModelObject =
        runCatching { clazz.getDeclaredConstructor().newInstance() }
            .getOrElse { error("Couldn't initialize FintModelObject") }

}
