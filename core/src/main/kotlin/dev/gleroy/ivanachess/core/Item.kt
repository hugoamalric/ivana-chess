package dev.gleroy.ivanachess.core

import java.io.Serializable

/**
 * Item.
 *
 * @param I Type of ID.
 */
interface Item<I : Serializable> {
    val id: I
}
