@file:Suppress("ClassName")

package dev.gleroy.ivanachess.dto

import dev.gleroy.ivanachess.core.Move
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class MoveDtoTest {
    @Nested
    inner class from {
        @Test
        fun `should return DTO`() {
            MoveDto.from(Move.Simple.fromCoordinates("A2", "A4")) shouldBe MoveDto(PositionDto(1, 2), PositionDto(1, 4))
        }
    }

    @Nested
    inner class convert {
        @Test
        fun `should convert to move`() {
            MoveDto(PositionDto(1, 2), PositionDto(1, 4)).convert() shouldBe Move.Simple.fromCoordinates("A2", "A4")
        }
    }
}
