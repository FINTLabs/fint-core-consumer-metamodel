fint-core-metamodel
===================

> **⚠️ Migration Notice**
> 1. **Library Rename:** This library has been renamed from `fint-core-consumer-metamodel` to `fint-core-metamodel` to reflect that its usage is not limited to consumers.
> 2. **Package Change:** The root package has changed from `no.fintlabs` to `no.novari`. Please update your imports accordingly.

Overview
--------
The **fint-core-metamodel** library provides a streamlined way to interact with FINT resource metadata through the `MetamodelService`. It is designed to simplify the exploration of components, resources, fields, identifiers, and relations defined within FINT’s ecosystem.

By using this library, you can programmatically access the cached metadata structure to build dynamic applications, validators, or tools without being tied to specific consumer logic.

Key Concepts
------------

### Resource
A `Resource` represents a specific FINT entity (e.g., `Elev`, `Skoleressurs`) containing metadata such as fields, identifiers, and relations.

**Definition:**
```kotlin
// package no.novari.metamodel.model

class Resource(
    name: String,
    val packageName: String,
    val resourceType: Class<out FintResource>,
    val isCommon: Boolean,
    val writeable: Boolean,
    val fields: Set<String>,
    val idFields: Set<String>,
    val relations: List<FintRelation>
)