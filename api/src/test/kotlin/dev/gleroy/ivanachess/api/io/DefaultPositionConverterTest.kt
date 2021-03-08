@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.Position
import dev.gleroy.ivanachess.dto.PositionDto
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultPositionConverterTest {
    private val converter = DefaultPositionConverter()

    @Nested
    inner class convertToDto {
        @Test
        fun `should return DTO`() {
            converter.convertToDto(Position.fromCoordinates("A1")) shouldBe PositionDto(1, 1)
        }
    }

    @Nested
    inner class convertToPosition {
        @Test
        fun `should return position`() {
            converter.convertToPosition(PositionDto(1, 1)) shouldBe Position.fromCoordinates("A1")
        }
    }
}
