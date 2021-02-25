package dev.gleroy.ivanachess.api

import java.time.OffsetDateTime
import java.util.*

/**
 * Entity.
 */
interface Entity {
    /**
     * ID.
     */
    val id: UUID

    /**
     * Creation date.
     */
    val creationDate: OffsetDateTime
}
