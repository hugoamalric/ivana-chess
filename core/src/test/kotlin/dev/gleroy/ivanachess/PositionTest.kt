@file:Suppress("ClassName")

package dev.gleroy.ivanachess

import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PositionTest {
    @Nested
    inner class constructor {
        @Test
        fun `should throw exception if col lower than 1`() {
            shouldThrowExceptionIfValueIsInvalid("col") { Position(0, 1) }
        }

        @Test
        fun `should throw exception if col greater than 8`() {
            shouldThrowExceptionIfValueIsInvalid("col") { Position(9, 1) }
        }

        @Test
        fun `should throw exception if row lower than 1`() {
            shouldThrowExceptionIfValueIsInvalid("row") { Position(1, 0) }
        }

        @Test
        fun `should throw exception if row greater than 8`() {
            shouldThrowExceptionIfValueIsInvalid("row") { Position(1, 9) }
        }

        private fun shouldThrowExceptionIfValueIsInvalid(propertyName: String, instantiate: () -> Position) {
            val exception = assertThrows<IllegalArgumentException> {
                instantiate()
            }
            exception shouldHaveMessage "$propertyName must be between ${Position.Min} and ${Position.Max}"
        }
    }

    @Nested
    inner class toString {
        @Test
        fun `should return A1`() {
            Position(1, 1).toString() shouldBe "A1"
        }

        @Test
        fun `should return B3`() {
            Position(2, 3).toString() shouldBe "B3"
        }
    }
}
