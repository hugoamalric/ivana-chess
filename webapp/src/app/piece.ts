import {Color} from './color.enum'
import {PieceType} from './piece-type.enum'
import {Position} from './position'

/**
 * Piece.
 */
export interface Piece {
  /**
   * Color.
   */
  color: Color

  /**
   * Type.
   */
  type: PieceType

  /**
   * Position.
   */
  pos: Position
}
