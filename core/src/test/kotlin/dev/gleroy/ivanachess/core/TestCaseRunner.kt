package dev.gleroy.ivanachess.core

import io.kotlintest.shouldBe

/**
 * Test case runner.
 */
class TestCaseRunner {
    /**
     * Board serializer.
     */
    private val serializer = StringBoardSerializer()

    /**
     * Run test case.
     *
     * @param testCase Test case.
     */
    fun run(testCase: TestCase) {
        println("Test case '${testCase.name}'")
        val game = testCase.moves.foldIndexed(Game()) { i, game, move ->
            val color = if (i % 2 == 0) Piece.Color.White else Piece.Color.Black
            printGame(game)
            println("Playing $move as $color")
            game.play(move)
        }
        val possibleMoves = game.nextPossibleMoves.map { it.move }.toSet()
        printGame(game)
        println("Moves: ${game.moves}")
        println("Possible moves: $possibleMoves")
        println("State: ${game.state}\n")
        game.board shouldBe testCase.board
        game.moves shouldBe testCase.moves
        game.state shouldBe testCase.gameState
        val unexpectedPossibleMoves = possibleMoves - testCase.possibleMoves
        if (unexpectedPossibleMoves.isNotEmpty()) {
            throw AssertionError("${unexpectedPossibleMoves.size} unexpected possible moves: $unexpectedPossibleMoves")
        }
        val missingPossibleMoves = testCase.possibleMoves - possibleMoves
        if (missingPossibleMoves.isNotEmpty()) {
            throw AssertionError("${missingPossibleMoves.size} missing possible moves: $missingPossibleMoves")
        }
    }

    /**
     * Print game on stdout.
     */
    private fun printGame(game: Game) {
        println(String(serializer.serialize(game.board)))
    }
}
