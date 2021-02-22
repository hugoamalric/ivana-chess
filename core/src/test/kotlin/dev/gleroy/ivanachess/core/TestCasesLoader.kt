package dev.gleroy.ivanachess.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.gleroy.ivanachess.dto.GameDto
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.toList

/**
 * Update test cases task.
 *
 * @param mapper Mapper.
 */
class TestCasesLoader(
    private val mapper: ObjectMapper = ObjectMapper().findAndRegisterModules()
) {
    private companion object {
        /**
         * JSON file extension.
         */
        private const val JsonExtension = ".json"

        /**
         * Root directory path.
         */
        private const val RootDirPath = "/test-cases"
    }

    /**
     * Load game test cases from JSON.
     *
     * @return Test cases.
     */
    fun load() = Files.list(Paths.get(javaClass.getResource(RootDirPath).toURI()))
        .filter { it.fileName.toString().endsWith(JsonExtension) }
        .map { loadTestCase(it) }
        .toList()

    /**
     * Load test case.
     *
     * @return Test case.
     */
    private fun loadTestCase(path: Path): TestCase {
        val testName = path.fileName.toString().let { it.substring(0, it.length - JsonExtension.length) }
        val gameDto = javaClass.getResourceAsStream("$RootDirPath/$testName.json").use { mapper.readValue<GameDto>(it) }
        val board = Board(
            pieceByPosition = gameDto.pieces
                .map { dto -> dto.convert().let { it.pos to it.piece } }
                .toMap()
        )
        return TestCase(
            name = testName,
            board = board,
            moves = gameDto.moves.mapIndexed { i, move ->
                val color = if (i % 2 == 0) Piece.Color.White else Piece.Color.Black
                move.convert(color)
            },
            possibleMoves = gameDto.possibleMoves.map { it.convert(gameDto.turnColor.coreColor) }.toSet(),
            gameState = gameDto.state.coreState,
            colorToPlay = gameDto.turnColor.coreColor
        )
    }
}
