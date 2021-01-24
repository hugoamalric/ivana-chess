package dev.gleroy.ivanachess.api

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.util.*

/**
 * In-memory implementation of game repository.
 */
@Repository
class InMemoryGameRepository : GameRepository {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(InMemoryGameRepository::class.java)
    }

    /**
     * Games.
     */
    internal val gameInfos = Collections.synchronizedSet(mutableSetOf<GameInfo>())

    override fun create(): GameInfo {
        val gameInfo = GameInfo()
        gameInfos.add(gameInfo)
        Logger.debug("New game created")
        return gameInfo
    }

    override fun getById(id: UUID) = gameInfos.find { it.id == id }.apply {
        if (this == null) {
            Logger.debug("No game found with ID $id")
        } else {
            Logger.debug("Game found with ID $id")
        }
    }

    override fun getByToken(token: UUID) = gameInfos.find { it.whiteToken == token || it.blackToken == token }.apply {
        if (this == null) {
            Logger.debug("No game found with token $token")
        } else {
            Logger.debug("Game found with token $token")
        }
    }

    override fun update(gameInfo: GameInfo): GameInfo {
        if (!gameInfos.removeIf { it.id == gameInfo.id }) {
            throw IllegalArgumentException("Game ${gameInfo.id} does not exist").apply { Logger.debug(message) }
        }
        gameInfos.add(gameInfo)
        Logger.debug("Game ${gameInfo.id} updated")
        return gameInfo
    }
}
