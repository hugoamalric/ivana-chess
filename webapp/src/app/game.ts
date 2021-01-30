import {Color} from './color.enum'
import {Piece} from './piece'
import {Move} from './move'
import {GameState} from './game-state.enum'

/**
 * Game.
 */
export interface Game {
  /**
   * ID.
   */
  id: string

  /**
   * White URL.
   */
  whiteUrl: string

  /**
   * Black URL.
   */
  blackUrl: string

  /**
   * Color to play.
   */
  colorToPlay: Color

  /**
   * State.
   */
  state: GameState

  /**
   * Pieces.
   */
  pieces: Piece[]

  /**
   * List of move since the begin of the game.
   */
  moves: Move[]

  /**
   * Possible moves.
   */
  possibleMoves: Move[]
}
