package dev.gleroy.ivanachess.api

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Default implementation of game service.
 *
 * @param repository Game repository.
 */
@Service
class DefaultGameService(
    private val repository: GameRepository
) : GameService {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(DefaultGameService::class.java)
    }

    override fun create(): GameInfo {
        val gameInfo = repository.create()
        Logger.info("New game created")
        return gameInfo
    }
}
