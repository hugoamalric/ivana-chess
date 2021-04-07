package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.api.*
import dev.gleroy.ivanachess.dto.PageDto

/**
 * Page converter.
 */
interface PageConverter {
    /**
     * Convert page to DTO.
     *
     * @param page Page.
     * @param convert Function to convert page content.
     * @return Page DTO.
     */
    fun <E, D> convertToDto(page: Page<E>, convert: (E) -> D): PageDto<D>

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
