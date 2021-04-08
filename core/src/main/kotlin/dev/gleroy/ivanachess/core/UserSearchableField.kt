package dev.gleroy.ivanachess.core

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
