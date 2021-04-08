@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.Position
import dev.gleroy.ivanachess.io.PositionRepresentation
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultPositionConverterTest {
    private val converter = DefaultPositionConverter()

    @Nested
    inner class convertToRepresentation {
        @Test
        fun `should return representation`() {
            converter.convertToRepresentation(Position.fromCoordinates("A1")) shouldBe PositionRepresentation(1, 1)
        }
    }

    @Nested
    inner class convertToPosition {
        @Test
        fun `should return position`() {
            converter.convertToPosition(PositionRepresentation(1, 1)) shouldBe Position.fromCoordinates("A1")
        }
    }
}
