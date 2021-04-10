@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.game.Position
import dev.gleroy.ivanachess.io.PageQueryParameters
import dev.gleroy.ivanachess.io.PageRepresentation
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DefaultPageConverterTest {
    private val converter = DefaultPageConverter()

    @Nested
    inner class convertToRepresentation {
        private val posConverter = DefaultPositionConverter()

        private val page = Page(
            content = listOf(Position.fromCoordinates("A1")),
            number = 1,
            totalPages = 100,
            totalItems = 1,
        )
        private val pageRepresentation = PageRepresentation(
            content = page.content.map { posConverter.convertToRepresentation(it) },
            number = page.number,
            totalPages = page.totalPages,
            totalItems = page.totalItems,
        )

        @Test
        fun `should return page representation`() {
            converter.convertToRepresentation(page, posConverter) shouldBe pageRepresentation
        }
    }

    @Nested
    inner class convertToOptions {
        private val pageParams = PageQueryParameters(
            page = 1,
            size = 10,
            sort = listOf(
                "-${CommonEntityField.CreationDate.label}",
                CommonEntityField.Id.label,
            ),
        )
        private val pageOpts = PageOptions(
            number = pageParams.page,
            size = pageParams.size,
            sorts = listOf(
                ItemSort(CommonEntityField.CreationDate, ItemSort.Order.Descending),
                ItemSort(CommonEntityField.Id),
            )
        )

        @Test
        fun `should throw exception if one of sortable fields is not supported`() {
            val unsupportedFieldLabel = "unsupported"
            val pageParams = pageParams.copy(sort = listOf(unsupportedFieldLabel))
            val exception = assertThrows<UnsupportedFieldException> { converter.convertToOptions(pageParams) }
            exception shouldBe UnsupportedFieldException(
                fieldLabel = unsupportedFieldLabel,
                supportedFields = CommonEntityField.values().toSet()
            )
        }

        @Test
        fun `should return page options`() {
            converter.convertToOptions(pageParams) shouldBe pageOpts
        }
    }
}
