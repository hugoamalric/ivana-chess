package dev.gleroy.ivanachess.io

/**
 * Web socket sender.
 */
interface WebSocketSender {
    /**
     * Send game.
     *
     * @param gameRepresentation Representation of game.
     */
    fun sendGame(gameRepresentation: GameRepresentation.Complete)
}
