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

    companion object {
        private val COMPONENTS_V4_0_0 = listOf(
            "arkiv.samferdsel",
            "utdanning.larling",
            "utdanning.vurdering",
            "administrasjon.kodeverk",
            "utdanning.kodeverk",
            "utdanning.elev",
            "okonomi.regnskap",
            "administrasjon.personal",
            "ressurs.tilgang",
            "arkiv.noark",
            "utdanning.ot",
            "felles.kodeverk",
            "ressurs.kodeverk",
            "utdanning.timeplan",
            "arkiv.kodeverk",
            "utdanning.utdanningsprogram",
            "arkiv.kulturminnevern",
            "ressurs.eiendel",
            "okonomi.faktura",
            "personvern.kodeverk",
            "personvern.samtykke",
            "arkiv.personal",
            "administrasjon.organisasjon",
            "okonomi.kodeverk",
            "administrasjon.fullmakt"
        )
    }

    @Autowired
    lateinit var service: MetamodelService

    @Nested
    @DisplayName("getResource")
    inner class GetResource {

        @Test
        fun `by domain+pkg+resource works`() {
            val res = service.getResource("utdanning", "vurdering", "elevfravar")
            assertNotNull(res)
            assertEquals("elevfravar", res!!.name)
        }

    }

    @Nested
    inner class GetComponent {

        @Test
        fun `FINT version 3_19 has expected number of components`() =
            assertEquals(COMPONENTS_V4_0_0.size, service.getComponents().size)

        @Test
        fun `FINT version 3_19 components match expected names`() {
            val componentNames = service.getComponents().map { "${it.domainName}.${it.packageName}" }.toSet()
            assertEquals(COMPONENTS_V4_0_0.toSet(), componentNames)
        }

    }

}