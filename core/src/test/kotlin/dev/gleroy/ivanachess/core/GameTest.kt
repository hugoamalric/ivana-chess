@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Files
import java.nio.file.Paths

internal class GameTest {
    private val game = Game()

    @Nested
    inner class colorToPlay {
        @Test
        fun `should return white`() {
            game.colorToPlay shouldBe Piece.Color.White
        }

        @Test
        fun `should return black`() {
            val move = Move.Simple(Position.fromCoordinates("A2"), Position.fromCoordinates("A4"))
            game.play(move).colorToPlay shouldBe Piece.Color.Black
        }
    }

    @Nested
    inner class possibleMoves {
        @Test
        fun `should return all next possible moves`() {
            game.nextPossibleMoves shouldBe game.board.pieces(Piece.Color.White)
                .flatMap { it.piece.possibleMoves(game.board, it.pos, game.moves) }
                .toSet()
        }
    }

    @Nested
    inner class state {
        private val deserializer = StringBoardDeserializer()

        @Test
        fun `should return Checkmate if player is checkmate`() {
            test("checkmate", Game.State.Checkmate)
        }

        @Test
        fun `should return Draw if player cant move any piece`() {
            test("draw", Game.State.Draw)
        }

        @Test
        fun `should return InGame if game is not over`() {
            test("in_game", Game.State.InGame)
        }

        private fun test(name: String, state: Game.State) {
            val path = Paths.get(javaClass.getResource("/game/$name.txt").toURI())
            val board = try {
                deserializer.deserialize(Files.newInputStream(path).readAllBytes())
            } catch (exception: IllegalArgumentException) {
                throw IllegalArgumentException("Unable to load $path: ${exception.message}")
            }
            val game = Game(board, listOf(Move.Simple.fromCoordinates("E2", "E4")))
            game.state shouldBe state
        }
    }

    @Nested
    inner class play {
        @Test
        fun `should throw exception if no piece at start position`() {
            val move = Move.Simple.fromCoordinates("A3", "A4")
            val exception = assertThrows<InvalidMoveException> { game.play(move) }
            exception shouldHaveMessage "No piece at ${move.from}"
        }

        @Test
        fun `should throw exception if piece is not white`() {
            val move = Move.Simple.fromCoordinates("A7", "A6")
            val exception = assertThrows<InvalidMoveException> { game.play(move) }
            exception shouldHaveMessage "Piece at ${move.from} is not white"
        }

        @Test
        fun `should throw exception if piece is not black`() {
            val move = Move.Simple.fromCoordinates("B2", "B4")
            val exception = assertThrows<InvalidMoveException> {
                game
                    .play(Move.Simple.fromCoordinates("A2", "A4"))
                    .play(move)
            }
            exception shouldHaveMessage "Piece at ${move.from} is not black"
        }

        @Test
        fun `should throw exception if move is invalid`() {
            val move = Move.Simple.fromCoordinates("A2", "A5")
            val exception = assertThrows<InvalidMoveException> { game.play(move) }
            exception shouldHaveMessage "Move from ${move.from} to ${move.to} is not allowed"
        }

        @Test
        fun `should return copy of the game with executed movement`() {
            val move = Move.Simple.fromCoordinates("A2", "A4")
            game.play(move) shouldBe Game(
                board = move.execute(game.board),
                moves = listOf(move)
            )
        }
    }
}
