package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.api.SearchableEntityField

/**
 * User searchable field.
 *
 * @param label Label.
 */
enum class UserSearchableField(
    override val label: String,
) : SearchableEntityField<User> {
    /**
     * Email.
     */
    Email("email"),

    /**
     * Pseudo.
     */
    Pseudo("pseudo")
}
