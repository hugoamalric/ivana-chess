package dev.gleroy.ivanachess.io

/**
 * Message sent when match between two players.
 *
 * @param whitePlayer Representation of white player.
 * @param blackPlayer Representation of black player.
 */
data class MatchQueueMessage(
    val whitePlayer: UserQueueMessage,
    val blackPlayer: UserQueueMessage,
)
