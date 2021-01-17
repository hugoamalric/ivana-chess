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
}
