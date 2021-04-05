package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.api.game.GameEntity
import dev.gleroy.ivanachess.api.game.Match
import dev.gleroy.ivanachess.dto.GameDto

/**
 * Game converter.
 */
interface GameConverter {
    /**
     * Convert game entity to DTO.
     *
     * @param gameEntity Game entity.
     * @return Game summary DTO.
     */
    fun convertToSummaryDto(gameEntity: GameEntity): GameDto.Summary

    /**
     * Convert entity to DTO.
     *
     * @param match Match.
     * @return Game DTO.
     */
    fun convertToCompleteDto(match: Match): GameDto.Complete
}
