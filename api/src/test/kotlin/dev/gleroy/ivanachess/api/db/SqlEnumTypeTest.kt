@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class SqlEnumTypeTest {
    private val type = DatabaseConstants.Type.Color

    @Nested
    inner class getValue {
        @Test
        fun `should throw exception if SQL enumeration value does not exist`() {
            val label = "unknown"
            val exception = assertThrows<IllegalArgumentException> { type.getValue("unknown") }
            exception shouldHaveMessage "Unknown value '$label' from type '${type.label}'"
        }

        @Test
        fun `should return SQL enumeration value`() {
            type.getValue(ColorSqlEnumValue.White.label) shouldBe ColorSqlEnumValue.White
        }
    }
}
