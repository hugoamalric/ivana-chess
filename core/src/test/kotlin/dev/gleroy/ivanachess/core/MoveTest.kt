@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MoveTest {
    private val board = Board.Initial

    @Nested
    inner class Simple {
        @Nested
        inner class fromCoordinates {
            @Test
            fun `should return move`() {
                val expected = Move.Simple(Position(1, 1), Position(2, 2))
                Move.Simple.fromCoordinates("A1", "B2") shouldBe expected
            }
        }

        @Nested
        inner class execute {
            @Test
            fun `should throw exception if no piece at start position`() {
                val move = Move.Simple.fromCoordinates("A3", "A4")
                val exception = assertThrows<IllegalArgumentException> { move.execute(board) }
                exception shouldHaveMessage "No piece at position ${move.from}"
            }

            @Test
            fun `should return new board with pawn moved from B2 to B3`() {
                val move = Move.Simple.fromCoordinates("B2", "B3")
                val pieceByPosition = board.pieceByPosition.toMutableMap()
                pieceByPosition.remove(move.from)
                pieceByPosition[move.to] = Piece.Pawn(Piece.Color.White)
                move.execute(board) shouldBe Board(pieceByPosition)
            }
        }
    }
}
