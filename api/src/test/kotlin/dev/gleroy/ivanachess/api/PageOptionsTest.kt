@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

import io.kotlintest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PageOptionsTest {
    @Nested
    inner class constructor {
        @Test
        fun `should throw exception if number is negative`() {
            shouldThrowExceptionIfNumberArgIsInvalid("number", -1, 1)
        }

        @Test
        fun `should throw exception if number is 0`() {
            shouldThrowExceptionIfNumberArgIsInvalid("number", 0, 1)
        }

        @Test
        fun `should throw exception if size is negative`() {
            shouldThrowExceptionIfNumberArgIsInvalid("size", 1, -1)
        }

        @Test
        fun `should throw exception if size is 0`() {
            shouldThrowExceptionIfNumberArgIsInvalid("size", 1, 0)
        }

        @Test
        fun `should throw exception if sorts list is empty`() {
            val exception = assertThrows<IllegalArgumentException> { PageOptions<Nothing>(1, 1, emptyList()) }
            exception shouldHaveMessage "sorts must not be empty"
        }

        private fun shouldThrowExceptionIfNumberArgIsInvalid(arg: String, number: Int, size: Int) {
            val exception = assertThrows<IllegalArgumentException> { PageOptions<Nothing>(number, size) }
            exception shouldHaveMessage "$arg must be strictly positive"
        }
    }
}
