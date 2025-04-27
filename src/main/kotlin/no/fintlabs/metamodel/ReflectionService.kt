package no.fintlabs.metamodel

import no.fint.model.FintModelObject
import org.reflections.Reflections
import org.springframework.stereotype.Service

@Service
class ReflectionService {

    val reflectionObjects: List<FintModelObject> = Reflections("no.fint.model")
        .getSubTypesOf(FintModelObject::class.java).map {
            initializeFintModelObject(it)
        }

    fun initializeFintModelObject(clazz: Class<out FintModelObject>): FintModelObject {
        try {
            return clazz.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            throw e
        }
    }

}
