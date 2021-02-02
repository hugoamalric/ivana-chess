import {Component, Input, OnInit} from '@angular/core'
import {Piece} from '../piece'

/**
 * Piece component.
 */
@Component({
  selector: 'app-piece',
  templateUrl: './piece.component.html',
  styleUrls: ['./piece.component.scss']
})
export class PieceComponent implements OnInit {
  /**
   * Piece.
   */
  @Input()
  piece: Piece | null = null

  constructor() {
  }

  ngOnInit(): void {
  }

}
