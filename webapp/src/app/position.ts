/**
 * Position.
 */
export interface Position {
  /**
   * Column index.
   */
  col: number

  /**
   * Row index.
   */
  row: number
}

/**
 * Check if position is equal to another.
 * @param pos Position.
 * @param col Column index.
 * @param row Row index.
 * @return True if position has given column and row indexes, false otherwise.
 */
export function positionEquals(pos: Position, col: number, row: number): boolean {
  return pos.col === col && pos.row === row
}
