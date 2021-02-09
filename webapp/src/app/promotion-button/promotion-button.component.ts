import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core'
import {PieceType} from '../piece-type.enum'
import {Color} from '../color.enum'

/**
 * Promotion button.
 */
@Component({
  selector: 'app-promotion-button',
  templateUrl: './promotion-button.component.html',
  styleUrls: ['./promotion-button.component.scss']
})
export class PromotionButtonComponent implements OnInit {
  /**
   * Piece type.
   */
  @Input()
  type: PieceType | null = null

  /**
   * Piece color.
   */
  @Input()
  color: Color | null = null

  @Output()
  promotionChoose = new EventEmitter<PieceType>()

  constructor() {
  }

  ngOnInit(): void {
  }

  /**
   * Handle click on promotion button.
   *
   * This method emit promotion choose event.
   */
  onClick(): void {
    if (this.type !== null) {
      this.promotionChoose.emit(this.type)
    }
  }
}
