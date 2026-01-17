package no.fintlabs.metamodel.model

import com.fasterxml.jackson.annotation.JsonIgnore
import io.mockk.every
import io.mockk.mockk
import no.fint.model.FintIdentifikator
import no.fint.model.FintModelObject
import no.fint.model.FintRelation
import no.fint.model.resource.FintResource
import no.fint.model.resource.Link
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ResourceTest {


    @Test
    fun `toRelationUri should handle COMMON relations (inherit from Component)`() {
        val component = Component("edu", "grading")
        val commonRelation = mockk<FintRelation> {
            every { packageName } returns "no.fint.model.felles.Person"
        }

        val uri = commonRelation.toRelationUri(component)

        assertEquals("edu/grading/person", uri)
    }

    @Test
    fun `toRelationUri should handle STANDARD relations (use own package)`() {
        val component = Component("ignored", "ignored")
        val standardRelation = mockk<FintRelation> {
            every { packageName } returns "no.fint.model.utdanning.vurdering.Elev"
        }

        val uri = standardRelation.toRelationUri(component)

        assertEquals("utdanning/vurdering/elev", uri)
    }

    @Test
    fun `createResource should map relations correctly`() {
        val component = Component("edu", "grading")

        val relation1 = mockk<FintRelation> {
            every { name } returns "elev"
            every { packageName } returns "no.fint.model.utdanning.vurdering.Elev"
        }
        val relation2 = mockk<FintRelation> {
            every { name } returns "person"
            every { packageName } returns "no.fint.model.felles.Person"
        }

        val testObject = object : TestModelObject() {
            override fun getRelations(): List<FintRelation> = listOf(relation1, relation2)
        }

        val resource = createResource(testObject, TestResource::class.java, component)

        val uris = resource.relationUri

        assertEquals("utdanning/vurdering/elev", uris["elev"])

        assertEquals("edu/grading/person", uris["person"])
    }

    @Test
    fun `createResource should ignore JsonIgnore, Static and Synthetic fields`() {
        val component = Component("test", "test")
        val testObject = TestModelObject()

        val resource = createResource(
            fintModelObject = testObject,
            resourceClass = TestResource::class.java,
            component = component
        )

        assertTrue(resource.fields.contains("validField"))
        assertFalse(resource.fields.contains("ignoredField"))
        assertFalse(resource.fields.contains("staticField"))
        assertTrue(resource.idFields.contains("systemId"))
        assertEquals("testmodelobject", resource.name)
    }

    class TestResource : FintResource {
        override fun getRelations(): List<FintRelation> = emptyList()
        override fun getLinks(): Map<String, List<Link>> = emptyMap()
        override fun getIdentifikators(): Map<String, FintIdentifikator> = emptyMap()
    }

    class TestIdentifikator(private var value: String = "") : FintIdentifikator {
        override fun getIdentifikatorverdi(): String = value
        override fun setIdentifikatorverdi(v: String) {
            value = v
        }
    }

    open class TestModelObject : FintModelObject {
        val validField: String = "ok"

        @JsonIgnore
        val ignoredField: String = "ignore me"

        companion object {
            @JvmField
            val staticField: String = "static"
        }

        override fun isWriteable(): Boolean = true
        override fun getIdentifikators(): Map<String, FintIdentifikator> = mapOf("systemId" to TestIdentifikator("1"))
        override fun getRelations(): List<FintRelation> = emptyList()
    }
}