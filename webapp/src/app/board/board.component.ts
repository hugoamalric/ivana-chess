import {Component, Input, OnInit} from '@angular/core'
import {Piece} from '../piece'
import {Color} from '../color.enum'

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
   * Pieces.
   */
  @Input()
  pieces: Piece[] = []

  /**
   * Color.
   */
  @Input()
  color: Color | null = null

  /**
   * Get column indexes.
   * @return Column indexes
   */
  columnIndexes(): number[] {
    return Array.from(Array(8).keys())
      .map(i => i + 1)
  }

  ngOnInit(): void {
  }

  /**
   * Get piece at position.
   * @param col Column index.
   * @param row Row index.
   * @return Piece or null if no piece at position.
   */
  pieceAt(col: number, row: number): Piece | null {
    const piece = this.pieces.find(piece => piece.pos.col === col && piece.pos.row === row)
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
