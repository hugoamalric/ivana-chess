import {Component, Input, OnInit} from '@angular/core'
import {Piece} from '../piece'
import {PieceType} from '../piece-type.enum'
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
   * Get black symbol.
   * @param piece Piece.
   * @return Black symbol.
   * @private
   */
  private static blackSymbol(piece: Piece): string {
    switch (piece.type) {
      case PieceType.Bishop:
        return '♝'
      case PieceType.King:
        return '♚'
      case PieceType.Knight:
        return '♞'
      case PieceType.Pawn:
        return '♟'
      case PieceType.Queen:
        return '♛'
      case PieceType.Rook:
        return '♜'
    }
  }

  /**
   * Get white symbol.
   * @param piece Piece.
   * @return White symbol.
   * @private
   */
  private static whiteSymbol(piece: Piece): string {
    switch (piece.type) {
      case PieceType.Bishop:
        return '♗'
      case PieceType.King:
        return '♔'
      case PieceType.Knight:
        return '♘'
      case PieceType.Pawn:
        return '♙'
      case PieceType.Queen:
        return '♕'
      case PieceType.Rook:
        return '♖'
    }
  }

  ngOnInit(): void {
  }

  /**
   * Get symbol at position.
   * @param col Column index.
   * @param row Row index.
   * @return Symbol or space if no piece at position.
   */
  symbolAt(col: number, row: number): string {
    const piece = this.pieces.find(piece => piece.pos.col === col && piece.pos.row === row)
    let symbol = ' '
    if (piece != null) {
      switch (piece.color) {
        case Color.White:
          symbol = BoardComponent.whiteSymbol(piece)
          break
        case Color.Black:
          symbol = BoardComponent.blackSymbol(piece)
          break
      }
    }
    return symbol
  }
}
