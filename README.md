# Metadata Service

This service extracts and exposes metadata from FINT libraries based on the version specified in the `gradle.properties` file. The metadata includes important information such as domain name, package name, resource name, id fields, relations, and more.

## Metadata Model

```kotlin
package no.fintlabs.metamodel.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Metadata(
    val domainName: String,
    val packageName: String?,
    val resourceName: String,
    val idFields: Set<String>,
    val writeable: Boolean,
    val relations: List<FintRelationMetadata>
)
```

### Fields:
- **domainName**: The domain the resource belongs to.
- **packageName**: The package name, nullable.
- **resourceName**: The name of the resource.
- **idFields**: A set of strings representing ID fields for the resource.
- **writeable**: A boolean indicating if the resource is writeable.
- **relations**: A list of related resources, represented by `FintRelationMetadata`.

## Fint Relation Metadata

```kotlin
package no.fintlabs.metamodel.model

import no.fint.model.FintMultiplicity

data class FintRelationMetadata(
    val name: String,
    val multiplicity: FintMultiplicity,
    val classPackageName: String
)
```

### Fields:
- **name**: The name of the relation.
- **multiplicity**: The multiplicity of the relation, provided by `FintMultiplicity`.
- **classPackageName**: The package name of the related class.
