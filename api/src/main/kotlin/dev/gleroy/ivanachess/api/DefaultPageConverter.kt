package dev.gleroy.ivanachess.api

import org.springframework.stereotype.Component

/**
 * Default implementation of page converter.
 */
@Component
class DefaultPageConverter : PageConverter {
    override fun <E, D> convert(page: Page<E>, convert: (E) -> D) = PageDto(
        content = page.content.map(convert),
        number = page.number,
        totalItems = page.totalItems,
        totalPages = page.totalPages
    )
}
