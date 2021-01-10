@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class BoardTest {
    @Nested
    inner class movePiece {
        private val board = Board.Initial

        @Test
        fun `should throw exception if no piece at starting position`() {
            val from = Position(3, 3)
            val exception = assertThrows<IllegalArgumentException> { board.movePiece(from, Position(4, 4)) }
            exception shouldHaveMessage "No piece at position $from"
        }

        @Test
        fun `should return with pawn moved from B2 to B3`() {
            val from = Position(2, 2)
            val to = Position(2, 3)
            val pieceByPosition = board.pieceByPosition.toMutableMap()
            pieceByPosition.remove(from)
            pieceByPosition[to] = Piece.Pawn(Piece.Color.White)
            board.movePiece(from, to) shouldBe Board(pieceByPosition)
        }
    }

    @Nested
    inner class pieceAtWithColAndRow : pieceAt() {
        override fun pieceAt(board: Board, col: Int, row: Int) = board.pieceAt(col, row)
    }

    @Nested
    inner class pieceAtWithPosition : pieceAt() {
        override fun pieceAt(board: Board, col: Int, row: Int) = board.pieceAt(Position(col, row))
    }

    @Nested
    inner class pieces {
        @Test
        fun `should return all white pieces`() {
            val color = Piece.Color.White
            val expectedPieces = setOf(
                PositionedPiece(Piece.Rook(color), Position(1, 1)),
                PositionedPiece(Piece.Knight(color), Position(2, 1)),
                PositionedPiece(Piece.Bishop(color), Position(3, 1)),
                PositionedPiece(Piece.King(color), Position(4, 1)),
                PositionedPiece(Piece.Queen(color), Position(5, 1)),
                PositionedPiece(Piece.Bishop(color), Position(6, 1)),
                PositionedPiece(Piece.Knight(color), Position(7, 1)),
                PositionedPiece(Piece.Rook(color), Position(8, 1))
            ) + (1..8).map { PositionedPiece(Piece.Pawn(color), Position(it, 2)) }
            val pieces = Board.Initial.pieces(Piece.Color.White)
            pieces shouldBe expectedPieces
        }
    }

    abstract class pieceAt {
        @Test
        fun `should return null if no piece at given position`() {
            pieceAt(Board.Initial, 1, 3).shouldBeNull()
        }

        @Test
        fun `should return white queen`() {
            pieceAt(Board.Initial, 5, 1) shouldBe Piece.Queen(Piece.Color.White)
        }

        protected abstract fun pieceAt(board: Board, col: Int, row: Int): Piece?
    }
}
