import {Position} from './position'
import {MoveType} from './move-type'
import {PieceType} from './piece-type.enum'

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

  /**
   * Promotion type.
   */
  promotionType: PieceType | null
}
