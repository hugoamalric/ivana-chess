package dev.gleroy.ivanachess.api

import java.time.OffsetDateTime
import java.util.*

/**
 * User.
 *
 * @param id ID.
 * @param pseudo Pseudo.
 * @param creationDate Creation date.
 * @param bcryptPassword BCrypt hash of password.
 */
data class User(
    override val id: UUID = UUID.randomUUID(),
    val pseudo: String,
    override val creationDate: OffsetDateTime = OffsetDateTime.now(),
    val bcryptPassword: String
) : Entity
