package dev.gleroy.ivanachess.api.db

/**
 * SQL enumeration type.
 *
 * @param V Type of enumeration.
 */
interface SqlEnumType<V> where V : SqlEnumValue, V : Enum<V> {
    /**
     * SQL label.
     */
    val label: String

    /**
     * Array of enumeration values.
     */
    val values: Array<V>

    /**
     * Get enumeration value from SQL label.
     *
     * @param label SQL label.
     * @return Enumeration value.
     * @throws IllegalArgumentException If [label] does not match any value.
     */
    @Throws(IllegalArgumentException::class)
    fun getValue(label: String) = values.find { it.label == label }
        ?: throw IllegalArgumentException("Unknown value '$label' from type '${this.label}'")
}
