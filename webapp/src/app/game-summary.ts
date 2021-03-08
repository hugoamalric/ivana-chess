import {Color} from './color.enum'
import {GameState} from './game-state.enum'
import {User} from './user'

/**
 * Game summary.
 */
export interface GameSummary {
  /**
   * ID.
   */
  id: string

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
}
