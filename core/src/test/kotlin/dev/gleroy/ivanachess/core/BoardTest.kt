@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class BoardTest {
    @Nested
    inner class pieceAtWithColAndRow : pieceAt() {
        override fun pieceAt(board: Board, col: Int, row: Int) = board.pieceAt(col, row)
    }

    @Nested
    inner class pieceAtWithPosition : pieceAt() {
        override fun pieceAt(board: Board, col: Int, row: Int) = board.pieceAt(Position(col, row))
    }

    @Nested
    inner class piecePositions {
        @Test
        fun `should return empty set if piece is not on board`() {
            Board(emptyMap()).piecePositions(Piece.King(Piece.Color.White)).shouldBeEmpty()
        }

        @Test
        fun `should return positions of given pieces`() {
            Board.Initial.piecePositions(Piece.Pawn(Piece.Color.White)) shouldBe setOf(
                Position.fromCoordinates("A2"),
                Position.fromCoordinates("B2"),
                Position.fromCoordinates("C2"),
                Position.fromCoordinates("D2"),
                Position.fromCoordinates("E2"),
                Position.fromCoordinates("F2"),
                Position.fromCoordinates("G2"),
                Position.fromCoordinates("H2"),
            )
        }
    }

    @Nested
    inner class pieces {
        @Test
        fun `should return all pieces`() {
            val expectedPieces = setOf(
                PositionedPiece(Piece.Rook(Piece.Color.White), Position(1, 1)),
                PositionedPiece(Piece.Knight(Piece.Color.White), Position(2, 1)),
                PositionedPiece(Piece.Bishop(Piece.Color.White), Position(3, 1)),
                PositionedPiece(Piece.Queen(Piece.Color.White), Position(4, 1)),
                PositionedPiece(Piece.King(Piece.Color.White), Position(5, 1)),
                PositionedPiece(Piece.Bishop(Piece.Color.White), Position(6, 1)),
                PositionedPiece(Piece.Knight(Piece.Color.White), Position(7, 1)),
                PositionedPiece(Piece.Rook(Piece.Color.White), Position(8, 1)),
                PositionedPiece(Piece.Pawn(Piece.Color.White), Position(1, 2)),
                PositionedPiece(Piece.Pawn(Piece.Color.White), Position(2, 2)),
                PositionedPiece(Piece.Pawn(Piece.Color.White), Position(3, 2)),
                PositionedPiece(Piece.Pawn(Piece.Color.White), Position(4, 2)),
                PositionedPiece(Piece.Pawn(Piece.Color.White), Position(5, 2)),
                PositionedPiece(Piece.Pawn(Piece.Color.White), Position(6, 2)),
                PositionedPiece(Piece.Pawn(Piece.Color.White), Position(7, 2)),
                PositionedPiece(Piece.Pawn(Piece.Color.White), Position(8, 2)),
                PositionedPiece(Piece.Rook(Piece.Color.Black), Position(1, 8)),
                PositionedPiece(Piece.Knight(Piece.Color.Black), Position(2, 8)),
                PositionedPiece(Piece.Bishop(Piece.Color.Black), Position(3, 8)),
                PositionedPiece(Piece.Queen(Piece.Color.Black), Position(4, 8)),
                PositionedPiece(Piece.King(Piece.Color.Black), Position(5, 8)),
                PositionedPiece(Piece.Bishop(Piece.Color.Black), Position(6, 8)),
                PositionedPiece(Piece.Knight(Piece.Color.Black), Position(7, 8)),
                PositionedPiece(Piece.Rook(Piece.Color.Black), Position(8, 8)),
                PositionedPiece(Piece.Pawn(Piece.Color.Black), Position(1, 7)),
                PositionedPiece(Piece.Pawn(Piece.Color.Black), Position(2, 7)),
                PositionedPiece(Piece.Pawn(Piece.Color.Black), Position(3, 7)),
                PositionedPiece(Piece.Pawn(Piece.Color.Black), Position(4, 7)),
                PositionedPiece(Piece.Pawn(Piece.Color.Black), Position(5, 7)),
                PositionedPiece(Piece.Pawn(Piece.Color.Black), Position(6, 7)),
                PositionedPiece(Piece.Pawn(Piece.Color.Black), Position(7, 7)),
                PositionedPiece(Piece.Pawn(Piece.Color.Black), Position(8, 7)),
            )
            val pieces = Board.Initial.pieces()
            pieces shouldBe expectedPieces
        }
    }

    @Nested
    inner class piecesWithColor {
        @Test
        fun `should return all white pieces`() {
            val color = Piece.Color.White
            val expectedPieces = setOf(
                PositionedPiece(Piece.Rook(color), Position(1, 1)),
                PositionedPiece(Piece.Knight(color), Position(2, 1)),
                PositionedPiece(Piece.Bishop(color), Position(3, 1)),
                PositionedPiece(Piece.Queen(color), Position(4, 1)),
                PositionedPiece(Piece.King(color), Position(5, 1)),
                PositionedPiece(Piece.Bishop(color), Position(6, 1)),
                PositionedPiece(Piece.Knight(color), Position(7, 1)),
                PositionedPiece(Piece.Rook(color), Position(8, 1))
            ) + (1..8).map { PositionedPiece(Piece.Pawn(color), Position(it, 2)) }
            val pieces = Board.Initial.pieces(Piece.Color.White)
            pieces shouldBe expectedPieces
        }
    }

    @Nested
    inner class promote {
        private val board = Board.Initial

        @Test
        fun `should throw exception if no piece at position`() {
            val pos = Position(3, 3)
            val exception = assertThrows<IllegalArgumentException> {
                board.promote(pos, Piece.Queen(Piece.Color.White))
            }
            exception shouldHaveMessage "No piece at position $pos"
        }

        @Test
        fun `should return new board with rook promoted to queen`() {
            val pos = Position(1, 8)
            val piece = Piece.Queen(Piece.Color.White)
            val pieceByPosition = board.pieceByPosition.toMutableMap()
            pieceByPosition[pos] = piece
            board.promote(pos, piece) shouldBe Board(pieceByPosition)
        }
    }

    abstract class pieceAt {
        @Test
        fun `should return null if no piece at given position`() {
            pieceAt(Board.Initial, 1, 3).shouldBeNull()
        }

        @Test
        fun `should return white king`() {
            pieceAt(Board.Initial, 5, 1) shouldBe Piece.King(Piece.Color.White)
        }

        protected abstract fun pieceAt(board: Board, col: Int, row: Int): Piece?
    }
}
