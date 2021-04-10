package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.core.*

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
     * @param sortableFields Set of sortable fields.
     * @return Page options.
     * @throws UnsupportedFieldException If one of sortable fields is not supported.
     */
    @Throws(UnsupportedFieldException::class)
    fun <E : Entity> convertToOptions(
        pageParams: PageQueryParameters,
        sortableFields: Set<SortableEntityField<E>> = emptySet()
    ): PageOptions<E>
}
