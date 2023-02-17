package no.nav.bidrag.commons.web.test

import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.mockk.CapturingSlot
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import java.util.*

@DisplayName("HttpHeaderTestRestTemplateTest")
internal class HttpHeaderTestRestTemplateTest {

    private lateinit var httpHeaderTestRestTemplate: HttpHeaderTestRestTemplate

    private val testRestTemplateMock: TestRestTemplate = mockk(relaxed = true)

    @BeforeEach
    fun initClassToTestWithCustomHeader() {
        httpHeaderTestRestTemplate = HttpHeaderTestRestTemplate(testRestTemplateMock)
        httpHeaderTestRestTemplate.add("X-Custom") { "my custom header" }
    }

    @Test
    @DisplayName("skal legge ved custom header verdi ved bruk")
    fun skalLeggeVedCustomHeaderVerdiVedBruk() {
        val slot = CapturingSlot<HttpEntity<*>>()

        httpHeaderTestRestTemplate.exchange("somewhere", null, HttpEntity<Any?>(null, null), Any::class.java)

        verify {
            testRestTemplateMock.exchange(any<String>(), any(), capture(slot), any<Class<*>>())
        }
        slot.captured shouldNotBe null
        val httpEntity = slot.captured
        httpEntity.headers shouldHaveSize 1
        httpEntity.headers["X-Custom"]!!.first() shouldContain "my custom header"
    }

    @Test
    @DisplayName("skal ikke overskrive eksisterende headere")
    fun skalIkkeOverskriveEksisterendeHeadere() {
        val headers = HttpHeaders().apply { add("something", "completely different") }
        val slot = CapturingSlot<HttpEntity<*>>()

        httpHeaderTestRestTemplate.exchange("somewhere", null, HttpEntity<Any?>(null, headers), Any::class.java)

        verify {
            testRestTemplateMock.exchange(any<String>(), any(), capture(slot), any<Class<*>>())
        }
        slot.captured shouldNotBe null
        slot.captured.headers shouldHaveSize 2
    }

    @Test
    @DisplayName("skal inititalisere ny HttpEntity når argument er null")
    fun skalInitialisereNyHttpEntityNaarArgumentErNull() {
        val typeReference: ParameterizedTypeReference<List<Any>> = ParameterizedTypeReference.forType(Any::class.java)
        val slot = CapturingSlot<HttpEntity<*>>()

        httpHeaderTestRestTemplate.exchange("somewhere", null, null, typeReference)

        verify {
            testRestTemplateMock.exchange(any<String>(), any(), capture(slot), eq(typeReference))
        }
        slot.captured shouldNotBe null
        val httpEntity = slot.captured
        httpEntity.headers shouldHaveSize 1
        httpEntity.headers["X-Custom"]!!.first() shouldContain "my custom header"
    }

    @Test
    @DisplayName("skal legge til HEADER som bare er gyldig for èn HttpEntity callback")
    fun skalLeggeTilHeaderSomErGyldigForEnHttpEntityCallback() {
        val typeReference: ParameterizedTypeReference<List<Any>> = ParameterizedTypeReference.forType(Any::class.java)

        httpHeaderTestRestTemplate.addHeaderForSingleHttpEntityCallback("X-OnlyOnce", "WithValue")
        httpHeaderTestRestTemplate.exchange("somewhere", null, null, typeReference)
        httpHeaderTestRestTemplate.exchange("somewhere", null, null, typeReference)

        val slot = mutableListOf<HttpEntity<*>>()
        verify(exactly = 2) {
            testRestTemplateMock.exchange(any<String>(), any(), capture(slot), eq(typeReference))
        }
        slot.first() shouldNotBe null
        val firstHttpEntity = slot.first()
        firstHttpEntity.headers shouldHaveSize 2
        firstHttpEntity.headers["X-Custom"]!!.first() shouldContain "my custom header"
        firstHttpEntity.headers["X-OnlyOnce"]!!.first() shouldContain "WithValue"
        slot[1] shouldNotBe null
        val httpEntity = slot[1]
        httpEntity.headers shouldHaveSize 1
        httpEntity.headers["X-Custom"]!!.first() shouldContain "my custom header"

    }

    @Nested
    @DisplayName("... as secured testRestTemplate")
    internal inner class Secured {
        @BeforeEach
        fun addSecurityHeader() {
            httpHeaderTestRestTemplate.add(HttpHeaders.AUTHORIZATION) { "Bearer secured" }
        }

        @Test
        @DisplayName("skal legge på OIDC token før exchange-kall med TestRestTemplate")
        fun skalLeggePaOidcToken() {
            httpHeaderTestRestTemplate.exchange("somewhere", null, HttpEntity<Any?>(null, null), Any::class.java)
            val entityCaptor = CapturingSlot<HttpEntity<*>>()
            verify {
                testRestTemplateMock.exchange(any<String>(), any(), capture(entityCaptor), eq(Any::class.java))
            }
            entityCaptor.captured shouldNotBe null
            val httpEntity = entityCaptor.captured
            httpEntity.headers shouldHaveSize 2   // custom and security header
            httpEntity.headers[HttpHeaders.AUTHORIZATION]!!.first() shouldContain "Bearer "
        }

        @Test
        @DisplayName("skal inititalisere ny HttpEntity når argument er null")
        fun skalInitialisereNyHttpEntityNaarArgumentErNull() {
            httpHeaderTestRestTemplate.exchange<List<Any>>("somewhere", null, null, ParameterizedTypeReference.forType(Any::class.java))
            val entityCaptor = CapturingSlot<HttpEntity<*>>()
            verify {
                testRestTemplateMock.exchange(
                    any<String>(), any(), capture(entityCaptor), any<ParameterizedTypeReference<List<Any>>>()
                )
            }

            entityCaptor.captured shouldNotBe null
            val httpEntity = entityCaptor.captured

            httpEntity.headers shouldHaveSize 2   // custom and security header
            httpEntity.headers[HttpHeaders.AUTHORIZATION]!!.first() shouldContain "Bearer "
        }
    }
}