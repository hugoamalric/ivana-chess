package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.core.GameEntity

/**
 * Game converter.
 */
interface GameConverter : ItemConverter<GameEntity, GameRepresentation.Summary>
