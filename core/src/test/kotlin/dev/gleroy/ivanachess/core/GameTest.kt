@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GameTest {
    private val testCases = TestCasesLoader().load()
    private val testCaseRunner = TestCaseRunner()
    private val game = Game()

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

        @Nested
        inner class `test cases` {
            @Test
            fun `001`() {
                val name = object {}.javaClass.enclosingMethod.name
                val testCase = testCases.find { it.name == name } ?: throw IllegalStateException("No test case '$name'")
                testCaseRunner.run(testCase)
            }

            @Test
            fun `002`() {
                val name = object {}.javaClass.enclosingMethod.name
                val testCase = testCases.find { it.name == name } ?: throw IllegalStateException("No test case '$name'")
                testCaseRunner.run(testCase)
            }

            @Test
            fun `003`() {
                val name = object {}.javaClass.enclosingMethod.name
                val testCase = testCases.find { it.name == name } ?: throw IllegalStateException("No test case '$name'")
                testCaseRunner.run(testCase)
            }

            @Test
            fun `004`() {
                val name = object {}.javaClass.enclosingMethod.name
                val testCase = testCases.find { it.name == name } ?: throw IllegalStateException("No test case '$name'")
                testCaseRunner.run(testCase)
            }

            @Test
            fun `005`() {
                val name = object {}.javaClass.enclosingMethod.name
                val testCase = testCases.find { it.name == name } ?: throw IllegalStateException("No test case '$name'")
                testCaseRunner.run(testCase)
            }

            @Test
            fun `006`() {
                val name = object {}.javaClass.enclosingMethod.name
                val testCase = testCases.find { it.name == name } ?: throw IllegalStateException("No test case '$name'")
                testCaseRunner.run(testCase)
            }

            @Test
            fun `007`() {
                val name = object {}.javaClass.enclosingMethod.name
                val testCase = testCases.find { it.name == name } ?: throw IllegalStateException("No test case '$name'")
                testCaseRunner.run(testCase)
            }

            @Test
            fun `008`() {
                val name = object {}.javaClass.enclosingMethod.name
                val testCase = testCases.find { it.name == name } ?: throw IllegalStateException("No test case '$name'")
                testCaseRunner.run(testCase)
            }

            @Test
            fun `009`() {
                val name = object {}.javaClass.enclosingMethod.name
                val testCase = testCases.find { it.name == name } ?: throw IllegalStateException("No test case '$name'")
                testCaseRunner.run(testCase)
            }

            @Test
            fun `010`() {
                val name = object {}.javaClass.enclosingMethod.name
                val testCase = testCases.find { it.name == name } ?: throw IllegalStateException("No test case '$name'")
                testCaseRunner.run(testCase)
            }

            @Test
            fun `011`() {
                val name = object {}.javaClass.enclosingMethod.name
                val testCase = testCases.find { it.name == name } ?: throw IllegalStateException("No test case '$name'")
                testCaseRunner.run(testCase)
            }
        }
    }
}
