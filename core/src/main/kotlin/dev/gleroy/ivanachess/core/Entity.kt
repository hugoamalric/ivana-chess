package dev.gleroy.ivanachess.core

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
