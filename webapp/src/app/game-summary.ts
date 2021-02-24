import {Color} from './color.enum'
import {GameState} from './game-state.enum'

/**
 * Game summary.
 */
export interface GameSummary {
  /**
   * ID.
   */
  id: string

  /**
   * Token to play as white player.
   */
  whiteToken: string

  /**
   * Token to play as black player.
   */
  blackToken: string

  /**
   * Color for which is turn to play.
   */
  turnColor: Color

  /**
   * State.
   */
  state: GameState
}
