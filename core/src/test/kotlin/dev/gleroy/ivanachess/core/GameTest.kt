@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GameTest {
    private val game = Game()

    @Nested
    inner class play {
        @Test
        fun `should throw exception if no piece at start position`() {
            val from = Position(3, 3)
            val exception = assertThrows<InvalidMoveException> { game.play(Move(from, Position(1, 1))) }
            exception shouldHaveMessage "No piece at $from"
        }

        @Test
        fun `should throw exception if piece is not white`() {
            val from = Position(1, 8)
            val exception = assertThrows<InvalidMoveException> { game.play(Move(from, Position(1, 1))) }
            exception shouldHaveMessage "Piece at $from is not white"
        }

        @Test
        fun `should throw exception if piece is not black`() {
            val from = Position(1, 4)
            val exception = assertThrows<InvalidMoveException> {
                game
                    .play(Move(Position(1, 2), from))
                    .play(Move(from, Position(1, 5)))
            }
            exception shouldHaveMessage "Piece at $from is not black"
        }

        @Test
        fun `should throw exception if move is invalid`() {
            val move = Move(Position(1, 2), Position(1, 5))
            val exception = assertThrows<InvalidMoveException> { game.play(move) }
            exception shouldHaveMessage "Move from ${move.from} to ${move.to} is not allowed"
        }

        @Test
        fun `should return copy of the game with executed movement`() {
            val move = Move(Position(1, 2), Position(1, 4))
            game.play(move) shouldBe Game(
                board = game.board.movePiece(move),
                moves = listOf(move)
            )
        }
    }
}
