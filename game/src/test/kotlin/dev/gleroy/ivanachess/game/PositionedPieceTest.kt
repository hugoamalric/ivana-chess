@file:Suppress("ClassName")

package dev.gleroy.ivanachess.game

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PositionedPieceTest {
    @Nested
    inner class toString {
        @Test
        fun `should return string representation`() {
            val piece = Piece.Knight(Piece.Color.White)
            val position = Position(1, 1)
            PositionedPiece(piece, position).toString() shouldBe "$position=$piece"
        }
    }
}
