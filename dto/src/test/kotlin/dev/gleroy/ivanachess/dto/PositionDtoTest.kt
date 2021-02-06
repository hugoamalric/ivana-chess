@file:Suppress("ClassName")

package dev.gleroy.ivanachess.dto

import dev.gleroy.ivanachess.core.Position
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PositionDtoTest {
    @Nested
    inner class from {
        @Test
        fun `should return DTO`() {
            PositionDto.from(Position.fromCoordinates("A1")) shouldBe PositionDto(1, 1)
        }
    }

    @Nested
    inner class convert {
        @Test
        fun `should convert to position`() {
            PositionDto(1, 1).convert() shouldBe Position.fromCoordinates("A1")
        }
    }
}
