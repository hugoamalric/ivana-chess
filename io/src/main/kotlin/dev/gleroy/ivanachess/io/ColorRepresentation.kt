package dev.gleroy.ivanachess.io

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Representation of color.
 */
enum class ColorRepresentation : Representation {
    /**
     * White.
     */
    @JsonProperty("white")
    White,

    /**
     * Black.
     */
    @JsonProperty("black")
    Black
}
