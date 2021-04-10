@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.game.Piece
import dev.gleroy.ivanachess.io.ColorRepresentation
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultColorConverterTest {
    private val converter = DefaultColorConverter()

    @Nested
    inner class convertToColor {
        @Test
        fun `should return white`() {
            converter.convertToColor(ColorRepresentation.White) shouldBe Piece.Color.White
        }

        @Test
        fun `should return black`() {
            converter.convertToColor(ColorRepresentation.Black) shouldBe Piece.Color.Black
        }
    }

    @Nested
    inner class convertToRepresentation {
        @Test
        fun `should return white`() {
            converter.convertToRepresentation(Piece.Color.White) shouldBe ColorRepresentation.White
        }

        @Test
        fun `should return black`() {
            converter.convertToRepresentation(Piece.Color.Black) shouldBe ColorRepresentation.Black
        }
    }
}
