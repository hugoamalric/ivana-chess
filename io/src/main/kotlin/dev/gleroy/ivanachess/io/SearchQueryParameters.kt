package dev.gleroy.ivanachess.io

import java.util.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * Search query parameters.
 *
 * @param q Search term.
 * @param field List of fields in which search.
 * @param exclude Set of IDs to exclude of the search.
 */
data class SearchQueryParameters(
    @field:NotNull
    val q: String? = null,

    @field:NotEmpty
    val field: List<String> = emptyList(),

    val exclude: Set<UUID> = emptySet(),
)
