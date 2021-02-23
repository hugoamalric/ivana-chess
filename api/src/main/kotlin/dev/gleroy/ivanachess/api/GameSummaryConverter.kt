package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.dto.GameDto

/**
 * Game summary converter.
 */
interface GameSummaryConverter {
    /**
     * Convert game summary to DTO.
     *
     * @param gameSummary Game summary.
     * @return Game summary DTO.
     */
    fun convert(gameSummary: GameSummary): GameDto.Summary

    /**
     * Convert game to DTO.
     *
     * @param gameSummary Game summary.
     * @param game Game.
     * @return Game DTO.
     */
    fun convert(gameSummary: GameSummary, game: Game): GameDto.Complete
}
