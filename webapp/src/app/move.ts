import {Position, positionEquals} from './position'
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
  promotionType: PieceType
}

/**
 * Check if move is equal to another.
 * @param move1 Move.
 * @param move2 Move.
 * @return True if move1 is equal to move2.
 */
export function moveEquals(move1: Move, move2: Move): boolean {
  return positionEquals(move1.from, move2.from.col, move2.from.row) &&
    positionEquals(move1.to, move2.to.col, move2.to.row)
}
