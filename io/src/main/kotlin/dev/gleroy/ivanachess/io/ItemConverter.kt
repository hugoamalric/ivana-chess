package dev.gleroy.ivanachess.io

/**
 * Item converter.
 *
 * @param T Type of item.
 * @param R Type of representation.
 */
fun interface ItemConverter<T, R : Representation> {
    /**
     * Convert item to its representation.
     *
     * @param item Item.
     * @return Representation of item.
     */
    fun convertToRepresentation(item: T): R
}
