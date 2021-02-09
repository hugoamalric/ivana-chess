import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core'
import {Piece} from '../piece'
import {Color} from '../color.enum'
import {Game} from '../game'
import {Position, positionEquals} from '../position'
import {Move} from '../move'

/**
 * Board component.
 */
@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.scss']
})
export class BoardComponent implements OnInit {
  /**
   * Possible positions.
   */
  possiblePositions: Position[] = []

  /**
   * Selected position.
   */
  selectedPosition: Position | null = null

  /**
   * Game.
   */
  @Input()
  game: Game | null = null

  /**
   * Color.
   */
  @Input()
  color: Color | null = null

  /**
   * Move event.
   */
  @Output()
  playMove: EventEmitter<Move> = new EventEmitter<Move>()

  /**
   * Get column indexes.
   * @return Column indexes
   */
  columnIndexes(): number[] {
    return Array.from(Array(8).keys())
      .map(i => i + 1)
  }

  /**
   * Check if current player is black.
   * @return True if current player is black, false otherwise.
   */
  isBlackPlayer(): boolean {
    return this.color === Color.Black
  }

  /**
   * Check if position is possible.
   * @param col Column index.
   * @param row Row index.
   * @return True if position is selected, false otherwise.
   */
  isPossiblePosition(col: number, row: number): boolean {
    return this.possiblePositions.filter(pos => positionEquals(pos, col, row)).length > 0
  }

  /**
   * Check if position is selected one.
   * @param col Column index.
   * @param row Row index.
   * @return True if position is selected one, false otherwise.
   */
  isSelectedPosition(col: number, row: number): boolean {
    return this.selectedPosition !== null && positionEquals(this.selectedPosition, col, row)
  }

  ngOnInit(): void {
  }

  /**
   * Handle position choose event.
   *
   * Emit move event.
   *
   * @param col Column index.
   * @param row Row index.
   */
  onChoose(col: number, row: number): void {
    if (this.game !== null && this.selectedPosition !== null) {
      const move = this.game.possibleMoves
        .find(move => {
          return positionEquals(move.from, this.selectedPosition!!.col, this.selectedPosition!!.row) &&
            positionEquals(move.to, col, row)
        })
      if (move !== undefined) {
        this.selectedPosition = null
        this.possiblePositions = []
        this.playMove.emit(move)
      }
    }
  }

  /**
   * Handle position select event.
   *
   * The selected position will be:
   *   - reset (and possible positions too) if given position is the same as current selected position;
   *   - set to given position and possible positions are displayed if it is player turn.
   *
   * @param col Column index.
   * @param row Row index.
   * @param piece Piece.
   */
  onSelect(col: number, row: number, piece: Piece | null): void {
    if (this.color !== null) {
      if (this.game && this.color === this.game.colorToPlay) {
        if (this.selectedPosition !== null && positionEquals(this.selectedPosition, col, row)) {
          this.selectedPosition = null
          this.possiblePositions = []
        } else if (piece?.color === this.color) {
          this.selectedPosition = {col, row}
          this.possiblePositions = this.game.possibleMoves
            .filter(move => positionEquals(move.from, col, row))
            .map(move => move.to)
        }
      }
    }
  }

  /**
   * Get piece at position.
   * @param col Column index.
   * @param row Row index.
   * @return Piece or null if no piece at position.
   */
  pieceAt(col: number, row: number): Piece | null {
    const piece = this.game?.pieces.find(piece => positionEquals(piece.pos, col, row))
    return piece === undefined ? null : piece
  }

  /**
   * Reset selected position.
   */
  resetSelected(): void {
    this.selectedPosition = null
  }

  /**
   * Get row indexes.
   * @return Row indexes
   */
  rowIndexes(): number[] {
    const indexes = Array.from(Array(8).keys())
      .map(i => i + 1)
    if (this.color === Color.White) {
      return indexes.reverse()
    }
    return indexes
  }
}
