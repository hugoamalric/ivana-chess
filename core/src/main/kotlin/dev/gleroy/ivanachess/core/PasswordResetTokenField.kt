package dev.gleroy.ivanachess.core

/**
 * Password reset token field.
 *
 * @param label Label.
 */
enum class PasswordResetTokenField(
    override val label: String
) : ItemField {
    /**
     * User.
     */
    User("user"),

    /**
     * Expiration date.
     */
    ExpirationDate("expirationDate");

    override val isSortable get() = true
    override val isFilterable get() = false
    override val isSearchable get() = false
}
