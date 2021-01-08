@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class BoardTest {
    @Nested
    inner class pieceAtWithColAndRow : pieceAt() {
        override fun pieceAt(board: Board, col: Int, row: Int) = board.pieceAt(col, row)
    }

    @Nested
    inner class pieceAtWithPosition : pieceAt() {
        override fun pieceAt(board: Board, col: Int, row: Int) = board.pieceAt(Position(col, row))
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
