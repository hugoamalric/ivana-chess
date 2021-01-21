package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.InvalidMoveException
import dev.gleroy.ivanachess.core.Move
import java.util.*
import kotlin.jvm.Throws

/**
 * Game service.
 */
interface GameService {
    /**
     * Create new game.
     *
     * @return Game information.
     */
    fun create(): GameInfo

    /**
     * Play move.
     *
     * @param token Token.
     * @param move Move.
     * @return Updated game.
     * @throws PlayException If an error occurs.
     */
    @Throws(PlayException::class)
    fun play(token: UUID, move: Move): GameInfo
}
