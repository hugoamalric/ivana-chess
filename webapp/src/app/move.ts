import {Position} from './position'
import {MoveType} from './move-type'

/**
 * Move.
 */
export interface Move {
  /**
   * Type.
   */
  type: MoveType

  /**
   * Start position.
   */
  from: Position

  /**
   * Target position.
   */
  to: Position
}
