package no.fintlabs.metamodel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [ApplicationTest::class])
class MetamodelServiceTest {

    @Autowired
    lateinit var service: MetamodelService

    @Nested
    @DisplayName("getResource")
    inner class GetResource {

        @Test
        fun `by component+resource works`() {
            val res = service.getResource("utdanning.vurdering", "elevfravar")
            assertNotNull(res)
            assertEquals("Elevfravar", res!!.name)
        }

        @Test
        fun `by domain+pkg+resource works`() {
            val res = service.getResource("utdanning", "vurdering", "elevfravar")
            assertNotNull(res)
            assertEquals("Elevfravar", res!!.name)
        }

    }

}