package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.core.ItemField
import dev.gleroy.ivanachess.core.Page
import dev.gleroy.ivanachess.core.PageOptions
import dev.gleroy.ivanachess.core.UnsupportedFieldException

/**
 * Page converter.
 */
interface PageConverter {
    /**
     * Convert page to its representation.
     *
     * @param page Page.
     * @param converter Item converter.
     * @return Representation of page.
     */
    fun <T, R : Representation> convertToRepresentation(
        page: Page<T>,
        converter: ItemConverter<T, R>
    ): PageRepresentation<R>

    /**
     * Convert page query parameters to page options.
     *
     * @param pageParams Page parameters.
     * @param fields Set of fields.
     * @return Page options.
     * @throws UnsupportedFieldException If one of sortable/filterable fields is not supported.
     */
    @Throws(UnsupportedFieldException::class)
    fun convertToOptions(pageParams: PageQueryParameters, fields: Array<out ItemField> = emptyArray()): PageOptions
}
