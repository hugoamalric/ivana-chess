import {Color} from './color.enum'
import {Piece} from './piece'
import {Move} from './move'
import {GameState} from './game-state.enum'
import {User} from './user'

/**
 * Game.
 */
export interface Game {
  /**
   * ID.
   */
  id: string

  /**
   * Creation date (ISO 8601).
   */
  creationDate: string

  /**
   * White player.
   */
  whitePlayer: User

  /**
   * Black player.
   */
  blackPlayer: User

  /**
   * Color for which is turn to play.
   */
  turnColor: Color

  /**
   * State.
   */
  state: GameState

  /**
   * Color of winner or null if the game is not checkmate.
   */
  winnerColor: Color | undefined

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
