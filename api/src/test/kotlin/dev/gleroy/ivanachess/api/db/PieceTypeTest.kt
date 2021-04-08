@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.game.Piece
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PieceTypeTest {
    @Nested
    inner class `from piece` {
        @Test
        fun `should return pawn`() {
            PieceType.from(Piece.Pawn(Piece.Color.White)) shouldBe PieceType.Pawn
        }

        @Test
        fun `should return rook`() {
            PieceType.from(Piece.Rook(Piece.Color.White)) shouldBe PieceType.Rook
        }

        @Test
        fun `should return knight`() {
            PieceType.from(Piece.Knight(Piece.Color.White)) shouldBe PieceType.Knight
        }

        @Test
        fun `should return bishop`() {
            PieceType.from(Piece.Bishop(Piece.Color.White)) shouldBe PieceType.Bishop
        }

        @Test
        fun `should return queen`() {
            PieceType.from(Piece.Queen(Piece.Color.White)) shouldBe PieceType.Queen
        }

        @Test
        fun `should return king`() {
            PieceType.from(Piece.King(Piece.Color.White)) shouldBe PieceType.King
        }
    }

    @Nested
    inner class `from SQL type value` {
        @Test
        fun `should throw exception if SQL type value is not a valid piece type`() {
            val sqlValue = "white"
            val exception = assertThrows<IllegalArgumentException> { PieceType.from(sqlValue) }
            exception shouldHaveMessage "Unknown piece type '$sqlValue'"
        }

        @Test
        fun `should return pawn`() {
            PieceType.from(PieceType.Pawn.sqlValue) shouldBe PieceType.Pawn
        }

        @Test
        fun `should return rook`() {
            PieceType.from(PieceType.Rook.sqlValue) shouldBe PieceType.Rook
        }

        @Test
        fun `should return knight`() {
            PieceType.from(PieceType.Knight.sqlValue) shouldBe PieceType.Knight
        }

        @Test
        fun `should return bishop`() {
            PieceType.from(PieceType.Bishop.sqlValue) shouldBe PieceType.Bishop
        }

        @Test
        fun `should return queen`() {
            PieceType.from(PieceType.Queen.sqlValue) shouldBe PieceType.Queen
        }

        @Test
        fun `should return king`() {
            PieceType.from(PieceType.King.sqlValue) shouldBe PieceType.King
        }
    }

    @Nested
    inner class Pawn {
        @Nested
        inner class instantiatePiece : PieceTypeTest.instantiatePiece(PieceType.Pawn) {
            override fun instantiatePiece(color: Piece.Color) = Piece.Pawn(color)
        }
    }

    @Nested
    inner class Rook {
        @Nested
        inner class instantiatePiece : PieceTypeTest.instantiatePiece(PieceType.Rook) {
            override fun instantiatePiece(color: Piece.Color) = Piece.Rook(color)
        }
    }

    @Nested
    inner class Knight {
        @Nested
        inner class instantiatePiece : PieceTypeTest.instantiatePiece(PieceType.Knight) {
            override fun instantiatePiece(color: Piece.Color) = Piece.Knight(color)
        }
    }

    @Nested
    inner class Bishop {
        @Nested
        inner class instantiatePiece : PieceTypeTest.instantiatePiece(PieceType.Bishop) {
            override fun instantiatePiece(color: Piece.Color) = Piece.Bishop(color)
        }
    }

    @Nested
    inner class Queen {
        @Nested
        inner class instantiatePiece : PieceTypeTest.instantiatePiece(PieceType.Queen) {
            override fun instantiatePiece(color: Piece.Color) = Piece.Queen(color)
        }
    }

    @Nested
    inner class King {
        @Nested
        inner class instantiatePiece : PieceTypeTest.instantiatePiece(PieceType.King) {
            override fun instantiatePiece(color: Piece.Color) = Piece.King(color)
        }
    }

    internal abstract class instantiatePiece(
        private val pieceType: PieceType
    ) {
        @Test
        fun `should return white piece`() {
            pieceType.instantiatePiece(Piece.Color.White) shouldBe instantiatePiece(Piece.Color.White)
        }

        @Test
        fun `should return black piece`() {
            pieceType.instantiatePiece(Piece.Color.Black) shouldBe instantiatePiece(Piece.Color.Black)
        }

        protected abstract fun instantiatePiece(color: Piece.Color): Piece
    }
}
