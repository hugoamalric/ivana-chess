import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core'
import {Piece} from '../piece'

/**
 * Position component.
 */
@Component({
  selector: 'app-position',
  templateUrl: './position.component.html',
  styleUrls: ['./position.component.scss']
})
export class PositionComponent implements OnInit {
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
  positionSelect: EventEmitter<void> = new EventEmitter<void>()

  constructor() {
  }

  ngOnInit(): void {
  }
}
