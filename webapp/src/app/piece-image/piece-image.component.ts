import {Component, Input, OnInit} from '@angular/core'
import {PieceType} from '../piece-type.enum'
import {Color} from '../color.enum'

@Component({
  selector: 'app-piece-image',
  templateUrl: './piece-image.component.html',
  styleUrls: ['./piece-image.component.scss']
})
export class PieceImageComponent implements OnInit {
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

  constructor() {
  }

  ngOnInit(): void {
  }

}
