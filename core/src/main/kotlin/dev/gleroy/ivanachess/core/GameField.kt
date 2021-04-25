package dev.gleroy.ivanachess.core

/**
 * Game field.
 *
 * @param label Label.
 */
enum class GameField(
    override val label: String,
) : ItemField {
    /**
     * State.
     */
    State("state");

    override val isSortable get() = false
    override val isFilterable get() = true
    override val isSearchable get() = false
}
