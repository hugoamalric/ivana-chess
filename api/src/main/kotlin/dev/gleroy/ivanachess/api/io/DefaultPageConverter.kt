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

    override fun convertToOptions(pageParams: PageQueryParameters, fields: Array<out ItemField>): PageOptions {
        val sortableFields = (fields.toSet() + CommonEntityField.values()).filter { it.isSortable }.toSet()
        val filterableFields = fields.filter { it.isFilterable }.toSet()
        return PageOptions(
            number = pageParams.page,
            size = pageParams.size,
            sorts = pageParams.sort.map { it.toItemSort(sortableFields) },
            filters = pageParams.filter.map { it.toItemFilter(filterableFields) }.toSet(),
        )
    }

    /**
     * Transform this string to item filter.
     *
     * @param filterableFields Set of filterable fields.
     * @return Item filter.
     * @throws UnsupportedFieldException If one of filterable fields is not supported.
     */
    @Throws(UnsupportedFieldException::class)
    private fun String.toItemFilter(filterableFields: Set<ItemField>): ItemFilter {
        val split = split(':')
        val field = filterableFields.find { it.label.equals(split[0], true) && it.isFilterable }
            ?: throw UnsupportedFieldException(split[0], filterableFields).apply { Logger.debug(message) }
        return ItemFilter(field, split.drop(1).joinToString(":"))
    }

    /**
     * Transform this string to item sort.
     *
     * @param sortableFields Set of sortable fields.
     * @return Item sort.
     * @throws UnsupportedFieldException If one of sortable fields is not supported.
     */
    @Throws(UnsupportedFieldException::class)
    private fun String.toItemSort(sortableFields: Set<ItemField>): ItemSort {
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
