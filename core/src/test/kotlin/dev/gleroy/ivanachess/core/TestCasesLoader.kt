package dev.gleroy.ivanachess.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.gleroy.ivanachess.dto.GameDto
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

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
            moves = gameDto.moves.map { it.convert() },
            possibleMoves = gameDto.possibleMoves.map { it.convert() }.toSet(),
            gameState = gameDto.state.coreState,
            colorToPlay = gameDto.colorToPlay.coreColor
        )
    }
}
