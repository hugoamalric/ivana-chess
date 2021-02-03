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
  play: EventEmitter<Move> = new EventEmitter<Move>()

  /**
   * Get column indexes.
   * @return Column indexes
   */
  columnIndexes(): number[] {
    return Array.from(Array(8).keys())
      .map(i => i + 1)
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
    if (this.selectedPosition !== null) {
      const move: Move = {
        from: this.selectedPosition,
        to: {col, row}
      }
      this.selectedPosition = null
      this.possiblePositions = []
      this.play.emit(move)
    }
  }

  /**
   * Handle position select event.
   *
   * The selected position will be changed and possible positions are displayed if it is player turn.
   *
   * @param col Column index.
   * @param row Row index.
   */
  onSelect(col: number, row: number): void {
    if (this.game && this.color === this.game.colorToPlay) {
      this.selectedPosition = {col, row}
      this.possiblePositions = this.game.possibleMoves
        .filter(move => positionEquals(move.from, col, row))
        .map(move => move.to)
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
