package no.fintlabs.metamodel.metadata

import jakarta.annotation.PostConstruct
import no.fintlabs.metamodel.ReflectionService
import no.fintlabs.metamodel.metadata.model.Resource
import org.springframework.stereotype.Service

@Service
class MetadataCacheInitializer(
    private val metadataCache: MetadataCache,
    reflectionService: ReflectionService
) {

    private val resourceMap =
        reflectionService.reflectionObjects
            .associate { it.javaClass.name to Resource.from(it) }

    @PostConstruct
    fun init() =
        resourceMap.values.forEach { resource ->
            val (domainName, packageName) = getDomainAndPackageName(resource.packageName)
            packageName?.let {
                resource.relations
                    .mapNotNull { relation -> resourceMap[relation.packageName] }
                    .filter { it.isCommon } + listOf(resource)
                    .forEach {
                        println("$domainName -> $packageName")
                        metadataCache.addResource(domainName, packageName, it)
                    }
            }
        }

    private fun getDomainAndPackageName(clazzPackageName: String): Pair<String, String?> =
        clazzPackageName.split(".").let { it[3] to it.getOrNull(4) }

}