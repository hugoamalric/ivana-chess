package dev.gleroy.ivanachess.api

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.util.*
import kotlin.math.ceil

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
    internal val gameInfos = Collections.synchronizedList(mutableListOf<GameInfo>())

    override fun create(): GameInfo {
        val gameInfo = GameInfo()
        gameInfos.add(gameInfo)
        Logger.debug("New game created")
        return gameInfo
    }

    override fun getAll(page: Int, size: Int): Page<GameInfo> {
        checkArgIsStrictlyPositive("page", page)
        checkArgIsStrictlyPositive("size", size)
        val offset = (page - 1) * size
        val totalPages = ceil(gameInfos.size.toDouble() / size.toDouble()).toInt()
        return if (offset >= gameInfos.size) {
            Page(
                totalItems = gameInfos.size,
                totalPages = totalPages
            )
        } else {
            val toIndex = minOf(offset + size, gameInfos.size)
            Page(
                content = gameInfos.subList(offset, toIndex),
                number = page,
                totalItems = gameInfos.size,
                totalPages = totalPages
            )
        }
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

    /**
     * Throw exception if value is negative.
     *
     * @param argName Argument name.
     * @param value Value.
     * @throws IllegalArgumentException If value is negative.
     */
    @Throws(IllegalArgumentException::class)
    private fun checkArgIsStrictlyPositive(argName: String, value: Int) {
        if (value < 0) {
            throw IllegalArgumentException("$argName must be strictly positive")
        }
    }
}
