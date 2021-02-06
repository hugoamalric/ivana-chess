@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.dto.PageDto
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultPageConverterTest {
    private val converter = DefaultPageConverter()

    @Nested
    inner class convert {
        private val page = Page(
            content = listOf("original"),
            number = 1,
            totalItems = 2,
            totalPages = 3
        )
        private val dto = PageDto(
            content = page.content.map { convert(it) },
            number = page.number,
            totalItems = page.totalItems,
            totalPages = page.totalPages
        )

        @Test
        fun `should return DTO`() {
            converter.convert(page) { convert(it) } shouldBe dto
        }

        private fun convert(str: String) = str.reversed()
    }
}
