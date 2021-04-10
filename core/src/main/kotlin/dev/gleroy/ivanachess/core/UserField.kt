package dev.gleroy.ivanachess.core

/**
 * User field.
 *
 * @param label Label.
 */
enum class UserField(
    override val label: String,
) : ItemField {
    /**
     * Email.
     */
    Email("email"),

    /**
     * Pseudo.
     */
    Pseudo("pseudo");

    override val isSortable get() = true
    override val isSearchable get() = true
}
