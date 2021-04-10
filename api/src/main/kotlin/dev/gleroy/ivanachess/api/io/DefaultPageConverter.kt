package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.io.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Default implementation of page converter.
 */
@Component
class DefaultPageConverter : PageConverter {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(DefaultPageConverter::class.java)
    }

    override fun <T, R : Representation> convertToRepresentation(page: Page<T>, converter: ItemConverter<T, R>) =
        PageRepresentation(
            content = page.content.map { converter.convertToRepresentation(it) },
            number = page.number,
            totalItems = page.totalItems,
            totalPages = page.totalPages,
        )

    override fun convertToOptions(pageParams: PageQueryParameters, fields: Array<out ItemField>) = PageOptions(
        number = pageParams.page,
        size = pageParams.size,
        sorts = pageParams.sort.map { fieldLabel ->
            fieldLabel.toEntitySort((fields.toSet() + CommonEntityField.values()).filter { it.isSortable }.toSet())
        },
    )

    /**
     * Transform this string to entity sort.
     *
     * @param sortableFields Set of sortable fields.
     * @return Entity sort.
     * @throws UnsupportedFieldException If one of sortable fields is not supported.
     */
    @Throws(UnsupportedFieldException::class)
    private fun String.toEntitySort(sortableFields: Set<ItemField>): ItemSort {
        val fieldLabel: String
        val order: ItemSort.Order
        if (startsWith('-')) {
            fieldLabel = substring(1)
            order = ItemSort.Order.Descending
        } else {
            fieldLabel = this
            order = ItemSort.Order.Ascending
        }
        val field = sortableFields.find { it.label.equals(fieldLabel, true) }
            ?: throw UnsupportedFieldException(fieldLabel, sortableFields).apply { Logger.debug(message) }
        return ItemSort(field, order)
    }
}
