fint-core-consumer-metamodel
============================

Overview
--------
The fint-core-consumer-metamodel library provides a simple way to interact with FINT resource metadata through the MetamodelGateway.
It is designed to make it easier to explore components, resources, fields, identifiers, and relations defined within FINT’s ecosystem.

By using this library, you can either fetch information directly via the API or process the resource metadata programmatically
for more advanced use cases.


Key Concepts
------------

Resource
--------
A Resource describes a FINT resource with its metadata, including fields, identifiers, and relations.
It is defined in no.fintlabs.metamodel.model.Resource:

    data class Resource(
        val name: String,
        val packageName: String,
        val component: Component,
        val resourceType: Class<out FintResource>,
        val isCommon: Boolean,
        val writeable: Boolean,
        val fields: Set<String>,
        val idFields: Set<String>,
        val relations: List<FintRelation>
    )

MetamodelGateway
----------------
The MetamodelGateway is the main entry point for interacting with the cached metamodel.
It allows you to:

- Retrieve components and their names
- Explore resources within a component or domain/package
- Access fields, ID fields, and relations of a resource
- Get resource names or relation names quickly

Example capabilities:
- getComponents(): List all components
- getResources(component: String): Get all resources for a component
- getResource(component: String, resource: String): Retrieve a single resource
- getFieldNames(component: String, resource: String): Get all field names
- getIdNames(component: String, resource: String): Get identifier field names
- getRelations(component: String, resource: String): Get relations
- getRelationNames(component: String, resource: String): Get relation names


MetamodelProperties
-------------------
You can change the format of how component names are viewed and fetched by configuring `MetamodelProperties`.

Defined in no.fintlabs.metamodel.config.MetamodelProperties:

    @Configuration
    @ConfigurationProperties("fint.metamodel")
    class MetamodelProperties {
        var format: String = "."
    }

By default, the separator for domain and package in component names is `"."`.
You can change it through application properties:

    fint.metamodel.format=_

This would make the component names be returned in the format `domain_package` instead of `domain.package`.


Example Usage
-------------
    val resources = metamodelGateway.getResources("utdanning.vurdering")
    resources.forEach {
        println("Resource: ${it.name}, Fields: ${it.fields}")
    }

    val studentResource = metamodelGateway.getResource("utdanning.elev", "elev")
    println("Student ID fields: ${studentResource.idFields}")
    println("Student relations: ${studentResource.relations}")
