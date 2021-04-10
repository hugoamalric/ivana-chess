package dev.gleroy.ivanachess.core

import java.time.OffsetDateTime
import java.util.*

/**
 * Entity.
 */
interface Entity : Item<UUID> {
    /**
     * Creation date.
     */
    val creationDate: OffsetDateTime
}
