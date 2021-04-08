@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.game.Piece
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ColorTypeTest {
    @Nested
    inner class `from color` {
        @Test
        fun `should return white`() {
            ColorType.from(Piece.Color.White) shouldBe ColorType.White
        }

        @Test
        fun `should return black`() {
            ColorType.from(Piece.Color.Black) shouldBe ColorType.Black
        }
    }

    @Nested
    inner class `from SQL type value` {
        @Test
        fun `should throw exception if SQL type value is not a valid color`() {
            val sqlValue = "pawn"
            val exception = assertThrows<IllegalArgumentException> { ColorType.from(sqlValue) }
            exception shouldHaveMessage "Unknown color '$sqlValue'"
        }

        @Test
        fun `should return white`() {
            ColorType.from(ColorType.White.sqlValue) shouldBe ColorType.White
        }

        @Test
        fun `should return black`() {
            ColorType.from(ColorType.Black.sqlValue) shouldBe ColorType.Black
        }
    }
}
