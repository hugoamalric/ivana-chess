package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.api.game.GameAndSummary
import dev.gleroy.ivanachess.api.game.GameSummary
import dev.gleroy.ivanachess.dto.GameDto

/**
 * Game converter.
 */
interface GameConverter {
    /**
     * Convert game summary to DTO.
     *
     * @param gameSummary Game summary.
     * @return Game summary DTO.
     */
    fun convertToSummaryDto(gameSummary: GameSummary): GameDto.Summary

    /**
     * Convert entity to DTO.
     *
     * @param gameAndSummary Game and summary.
     * @return Game DTO.
     */
    fun convertToCompleteDto(gameAndSummary: GameAndSummary): GameDto.Complete
}
