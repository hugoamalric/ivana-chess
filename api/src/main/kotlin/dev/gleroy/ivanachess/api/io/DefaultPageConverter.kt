package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.api.*
import dev.gleroy.ivanachess.dto.PageDto
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

    override fun <E, D> convertToDto(page: Page<E>, convert: (E) -> D) = PageDto(
        content = page.content.map(convert),
        number = page.number,
        totalItems = page.totalItems,
        totalPages = page.totalPages
    )

    override fun <E : Entity> convertToOptions(
        pageParams: PageQueryParameters,
        sortableFields: Set<SortableEntityField<E>>
    ) = PageOptions(
        number = pageParams.page,
        size = pageParams.size,
        sorts = pageParams.sort.map { it.toEntitySort(sortableFields + CommonSortableEntityField.values()) }
    )

    /**
     * Transform this string to entity sort.
     *
     * @param sortableFields Set of sortable fields.
     * @return Entity sort.
     * @throws UnsupportedFieldException If one of sortable fields is not supported.
     */
    @Throws(UnsupportedFieldException::class)
    private fun <E : Entity> String.toEntitySort(sortableFields: Set<SortableEntityField<E>>): EntitySort<E> {
        val fieldLabel: String
        val order: EntitySort.Order
        if (startsWith('-')) {
            fieldLabel = substring(1)
            order = EntitySort.Order.Descending
        } else {
            fieldLabel = this
            order = EntitySort.Order.Ascending
        }
        val field = sortableFields.find { it.label.equals(fieldLabel, true) }
            ?: throw UnsupportedFieldException(fieldLabel, sortableFields).apply { Logger.debug(message) }
        return EntitySort(field, order)
    }
}
