package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.api.Page
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
    fun <E, D> convert(page: Page<E>, convert: (E) -> D): PageDto<D>
}
