@file:Suppress("ClassName")

package dev.gleroy.ivanachess.dto

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class JacksonTrimStringDeserializerTest {
    private val deserializer = JacksonTrimStringDeserializer()

    @Nested
    inner class deserialize {
        private lateinit var parser: JsonParser
        private lateinit var context: DeserializationContext

        @BeforeEach
        fun beforeEach() {
            parser = mockk()
            context = mockk()
        }

        @Test
        fun `should return null`() {
            every { parser.valueAsString } returns null
            deserializer.deserialize(parser, context).shouldBeNull()
            verify { parser.valueAsString }
            confirmVerified(parser)
        }

        @Test
        fun `should return trimmed string`() {
            val value = " value "
            every { parser.valueAsString } returns value
            deserializer.deserialize(parser, context) shouldBe value.trim()
            verify { parser.valueAsString }
            confirmVerified(parser)
        }
    }
}
