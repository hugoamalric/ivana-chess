@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.game.Piece
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PieceTypeSqlEnumValueTest {
    @Nested
    inner class `from piece` {
        @Test
        fun `should return pawn`() {
            PieceTypeSqlEnumValue.from(Piece.Pawn(Piece.Color.White)) shouldBe PieceTypeSqlEnumValue.Pawn
        }

        @Test
        fun `should return rook`() {
            PieceTypeSqlEnumValue.from(Piece.Rook(Piece.Color.White)) shouldBe PieceTypeSqlEnumValue.Rook
        }

        @Test
        fun `should return knight`() {
            PieceTypeSqlEnumValue.from(Piece.Knight(Piece.Color.White)) shouldBe PieceTypeSqlEnumValue.Knight
        }

        @Test
        fun `should return bishop`() {
            PieceTypeSqlEnumValue.from(Piece.Bishop(Piece.Color.White)) shouldBe PieceTypeSqlEnumValue.Bishop
        }

        @Test
        fun `should return queen`() {
            PieceTypeSqlEnumValue.from(Piece.Queen(Piece.Color.White)) shouldBe PieceTypeSqlEnumValue.Queen
        }

        @Test
        fun `should return king`() {
            PieceTypeSqlEnumValue.from(Piece.King(Piece.Color.White)) shouldBe PieceTypeSqlEnumValue.King
        }
    }

    @Nested
    inner class Pawn {
        @Nested
        inner class instantiatePiece : PieceTypeSqlEnumValueTest.instantiatePiece(PieceTypeSqlEnumValue.Pawn) {
            override fun instantiatePiece(color: Piece.Color) = Piece.Pawn(color)
        }
    }

    @Nested
    inner class Rook {
        @Nested
        inner class instantiatePiece : PieceTypeSqlEnumValueTest.instantiatePiece(PieceTypeSqlEnumValue.Rook) {
            override fun instantiatePiece(color: Piece.Color) = Piece.Rook(color)
        }
    }

    @Nested
    inner class Knight {
        @Nested
        inner class instantiatePiece : PieceTypeSqlEnumValueTest.instantiatePiece(PieceTypeSqlEnumValue.Knight) {
            override fun instantiatePiece(color: Piece.Color) = Piece.Knight(color)
        }
    }

    @Nested
    inner class Bishop {
        @Nested
        inner class instantiatePiece : PieceTypeSqlEnumValueTest.instantiatePiece(PieceTypeSqlEnumValue.Bishop) {
            override fun instantiatePiece(color: Piece.Color) = Piece.Bishop(color)
        }
    }

    @Nested
    inner class Queen {
        @Nested
        inner class instantiatePiece : PieceTypeSqlEnumValueTest.instantiatePiece(PieceTypeSqlEnumValue.Queen) {
            override fun instantiatePiece(color: Piece.Color) = Piece.Queen(color)
        }
    }

    @Nested
    inner class King {
        @Nested
        inner class instantiatePiece : PieceTypeSqlEnumValueTest.instantiatePiece(PieceTypeSqlEnumValue.King) {
            override fun instantiatePiece(color: Piece.Color) = Piece.King(color)
        }
    }

    internal abstract class instantiatePiece(
        private val pieceType: PieceTypeSqlEnumValue
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
