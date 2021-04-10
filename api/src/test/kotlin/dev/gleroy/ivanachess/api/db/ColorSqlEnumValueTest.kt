@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.game.Piece
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ColorSqlEnumValueTest {
    @Nested
    inner class `from color` {
        @Test
        fun `should return white`() {
            ColorSqlEnumValue.from(Piece.Color.White) shouldBe ColorSqlEnumValue.White
        }

        @Test
        fun `should return black`() {
            ColorSqlEnumValue.from(Piece.Color.Black) shouldBe ColorSqlEnumValue.Black
        }
    }
}
