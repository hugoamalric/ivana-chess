import {Position} from './position'

/**
 * Move.
 */
export interface Move {
  /**
   * Start position.
   */
  from: Position

  /**
   * Target position.
   */
  to: Position
}
