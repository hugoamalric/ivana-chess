package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.api.SortableEntityField

/**
 * User sortable field.
 *
 * @param label Label.
 */
enum class UserSortableField(
    override val label: String,
) : SortableEntityField<User> {
    /**
     * Email.
     */
    Email("email"),

    /**
     * Pseudo.
     */
    Pseudo("pseudo")
}
