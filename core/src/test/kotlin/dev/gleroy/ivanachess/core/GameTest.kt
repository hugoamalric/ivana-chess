@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GameTest {
    @Nested
    inner class play {
        private val game = Game()
        private val mapper = ObjectMapper().findAndRegisterModules()
        private val serializer = AsciiBoardSerializer()

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

        @Nested
        inner class `test cases` {
            @Test
            fun `001`() {
                val name = object {}.javaClass.enclosingMethod.name
                test(name)
            }

            @Test
            fun `002`() {
                val name = object {}.javaClass.enclosingMethod.name
                test(name)
            }

            @Test
            fun `003`() {
                val name = object {}.javaClass.enclosingMethod.name
                test(name)
            }

            @Test
            fun `004`() {
                val name = object {}.javaClass.enclosingMethod.name
                test(name)
            }

            @Test
            fun `005`() {
                val name = object {}.javaClass.enclosingMethod.name
                test(name)
            }

            @Test
            fun `006`() {
                val name = object {}.javaClass.enclosingMethod.name
                test(name)
            }

            @Test
            fun `007`() {
                val name = object {}.javaClass.enclosingMethod.name
                test(name)
            }

            @Test
            fun `008`() {
                val name = object {}.javaClass.enclosingMethod.name
                test(name)
            }

            @Test
            fun `009`() {
                val name = object {}.javaClass.enclosingMethod.name
                test(name)
            }

            @Test
            fun `010`() {
                val name = object {}.javaClass.enclosingMethod.name
                test(name)
            }

            @Test
            fun `011`() {
                val name = object {}.javaClass.enclosingMethod.name
                test(name)
            }

            @Test
            fun `012`() {
                val name = object {}.javaClass.enclosingMethod.name
                test(name)
            }

            @Test
            fun `013`() {
                val name = object {}.javaClass.enclosingMethod.name
                test(name)
            }

            @Test
            fun `014`() {
                val name = object {}.javaClass.enclosingMethod.name
                test(name)
            }

            private fun printGame(game: Game) {
                println(String(serializer.serialize(game.board)))
            }

            private fun test(name: String) {
                val testCaseDto = javaClass.getResourceAsStream("/test-cases/$name.json").use { input ->
                    mapper.readValue<TestCaseDto>(input)
                }
                val board = Board(
                    pieceByPosition = testCaseDto.pieces
                        .map { dto -> dto.convert().let { it.pos to it.piece } }
                        .toMap()
                )
                val moves = testCaseDto.moves.map { it.convert() }
                val expectedPossibleMoves = testCaseDto.possibleMoves.map { it.convert() }
                val game = moves.foldIndexed(Game()) { i, game, move ->
                    val color = if (i % 2 == 0) Piece.Color.White else Piece.Color.Black
                    printGame(game)
                    println("Playing $move as $color")
                    game.play(move)
                }
                val possibleMoves = game.nextPossibleMoves.map { it.move }.toSet()
                printGame(game)
                game.turnColor shouldBe testCaseDto.turnColor.coreColor
                game.state shouldBe testCaseDto.state.coreState
                game.board shouldBe board
                game.moves shouldBe moves
                val unexpectedPossibleMoves = possibleMoves - expectedPossibleMoves
                if (unexpectedPossibleMoves.isNotEmpty()) {
                    throw AssertionError(
                        "${unexpectedPossibleMoves.size} unexpected possible moves: $unexpectedPossibleMoves"
                    )
                }
                val missingPossibleMoves = expectedPossibleMoves - possibleMoves
                if (missingPossibleMoves.isNotEmpty()) {
                    throw AssertionError(
                        "${missingPossibleMoves.size} missing possible moves: $missingPossibleMoves"
                    )
                }
            }
        }
    }
}
