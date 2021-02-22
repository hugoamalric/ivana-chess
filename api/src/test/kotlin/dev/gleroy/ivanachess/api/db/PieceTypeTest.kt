@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.Piece
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PieceTypeTest {
    @Nested
    inner class fromPiece {
        @Test
        fun `should return pawn`() {
            PieceType.fromPiece(Piece.Pawn(Piece.Color.White)) shouldBe PieceType.Pawn
        }

        @Test
        fun `should return rook`() {
            PieceType.fromPiece(Piece.Rook(Piece.Color.White)) shouldBe PieceType.Rook
        }

        @Test
        fun `should return knight`() {
            PieceType.fromPiece(Piece.Knight(Piece.Color.White)) shouldBe PieceType.Knight
        }

        @Test
        fun `should return bishop`() {
            PieceType.fromPiece(Piece.Bishop(Piece.Color.White)) shouldBe PieceType.Bishop
        }

        @Test
        fun `should return queen`() {
            PieceType.fromPiece(Piece.Queen(Piece.Color.White)) shouldBe PieceType.Queen
        }

        @Test
        fun `should return king`() {
            PieceType.fromPiece(Piece.King(Piece.Color.White)) shouldBe PieceType.King
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
