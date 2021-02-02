import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core'
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

  /**
   * If this position is possible.
   */
  @Input()
  possible: boolean = false

  /**
   * If this position is selected.
   */
  @Input()
  selected: boolean = false

  /**
   * Select event.
   */
  @Output()
  select: EventEmitter<void> = new EventEmitter<void>()

  constructor() {
  }

  ngOnInit(): void {
  }
}
