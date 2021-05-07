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
            converter.convertToRepresentation(page) {
                posConverter.convertToRepresentation(it)
            } shouldBe pageRepresentation
        }
    }

    @Nested
    inner class convertToOptions {
        private val pseudoFilterValue = "user99"
        private val pageParams = PageQueryParameters(
            page = 1,
            size = 10,
            sort = listOf(
                "-${CommonEntityField.CreationDate.label}",
                CommonEntityField.Id.label,
            ),
            filter = setOf("pseudo:$pseudoFilterValue"),
        )
        private val pageOpts = PageOptions(
            number = pageParams.page,
            size = pageParams.size,
            sorts = listOf(
                ItemSort(CommonEntityField.CreationDate, ItemSort.Order.Descending),
                ItemSort(CommonEntityField.Id),
            ),
            filters = setOf(ItemFilter(UserField.Pseudo, pseudoFilterValue)),
        )

        @Test
        fun `should throw exception if one of sortable fields is not supported`() {
            val fieldLabel = "unsupported"
            val pageParams = pageParams.copy(sort = listOf(fieldLabel))
            val exception = assertThrows<UnsupportedFieldException> { converter.convertToOptions(pageParams) }
            exception shouldBe UnsupportedFieldException(
                fieldLabel = fieldLabel,
                supportedFields = CommonEntityField.values().toSet()
            )
        }

        @Test
        fun `should throw exception if one of filterable fields is not supported`() {
            val fieldLabel = "unsupported"
            val pageParams = pageParams.copy(filter = setOf("$fieldLabel:user99"))
            val exception = assertThrows<UnsupportedFieldException> { converter.convertToOptions(pageParams) }
            exception shouldBe UnsupportedFieldException(
                fieldLabel = fieldLabel,
                supportedFields = emptySet()
            )
        }

        @Test
        fun `should return page options`() {
            converter.convertToOptions(pageParams, UserField.values()) shouldBe pageOpts
        }
    }
}
