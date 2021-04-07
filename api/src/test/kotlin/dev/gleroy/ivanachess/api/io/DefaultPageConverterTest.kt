@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.api.*
import dev.gleroy.ivanachess.api.game.GameEntity
import dev.gleroy.ivanachess.dto.PageDto
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DefaultPageConverterTest {
    private val converter = DefaultPageConverter()

    @Nested
    inner class convertToDto {
        private val page = Page(
            content = listOf("str"),
            number = 1,
            totalPages = 100,
            totalItems = 1,
        )
        private val dto = PageDto(
            content = page.content.map { convertContent(it) },
            number = page.number,
            totalPages = page.totalPages,
            totalItems = page.totalItems,
        )

        @Test
        fun `should return page DTO`() {
            converter.convertToDto(page) { convertContent(it) } shouldBe dto
        }

        private fun convertContent(str: String) = str.reversed()
    }

    @Nested
    inner class convertToOptions {
        private val pageParams = PageQueryParameters(
            page = 1,
            size = 10,
            sort = listOf(
                "-${CommonSortableEntityField.CreationDate.label}",
                CommonSortableEntityField.Id.label,
            ),
        )
        private val pageOpts = PageOptions<GameEntity>(
            number = pageParams.page,
            size = pageParams.size,
            sorts = listOf(
                EntitySort(CommonSortableEntityField.CreationDate, EntitySort.Order.Descending),
                EntitySort(CommonSortableEntityField.Id),
            )
        )

        @Test
        fun `should throw exception if one of sortable fields is not supported`() {
            val unsupportedFieldLabel = "unsupported"
            val pageParams = pageParams.copy(sort = listOf(unsupportedFieldLabel))
            val exception = assertThrows<UnsupportedFieldException> {
                converter.convertToOptions<GameEntity>(pageParams)
            }
            exception shouldBe UnsupportedFieldException(
                fieldLabel = unsupportedFieldLabel,
                supportedFields = CommonSortableEntityField.values().toSet()
            )
        }

        @Test
        fun `should return page options`() {
            converter.convertToOptions<GameEntity>(pageParams) shouldBe pageOpts
        }
    }
}
