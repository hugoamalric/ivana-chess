import {Component, OnInit} from '@angular/core'
import {ActivatedRoute} from '@angular/router'
import {GameService} from '../game.service'
import {Game} from '../game'
import {Location} from '@angular/common'
import {Color} from '../color.enum'
import {Move, moveEquals} from '../move'
import {MoveType} from '../move-type'
import {PieceType} from '../piece-type.enum'
import {HistoryService} from '../history.service'

/**
 * Game component.
 */
@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit {
  /**
   * Game.
   */
  game: Game | null = null

  /**
   * Player token.
   */
  token: string | null = null

  /**
   * Player color.
   */
  color: Color | null = null

  /**
   * Selected move.
   */
  promotionMove: Move | null = null

  /**
   * Promotion piece types.
   */
  promotionTypes = [PieceType.Rook, PieceType.Knight, PieceType.Bishop, PieceType.Queen]

  /**
   * Initialize component.
   *
   * @param gameService Game service.
   * @param historyService History service.
   * @param route Current route.
   * @param location Current location.
   */
  constructor(
    private gameService: GameService,
    private historyService: HistoryService,
    private route: ActivatedRoute,
    private location: Location
  ) {
  }

  /**
   * Choose promotion.
   *
   * @param type Promotion piece type.
   */
  choosePromotion(type: PieceType): void {
    if (this.game !== null && this.promotionMove !== null && this.token !== null) {
      const move = this.game.possibleMoves.find(move => moveEquals(this.promotionMove!!, move) && move.promotionType === type)
      if (move !== undefined) {
        this.promotionMove = null
        this.gameService.play(this.token, move).subscribe()
      }
    }
  }

  /**
   * Go back.
   */
  goBack(): void {
    this.historyService.goBack('/')
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id')
      this.gameService.getGame(id!!).subscribe(game => {
        this.game = game
        this.gameService.watchGame(this.game.id).subscribe(game => this.game = game)
        this.route.queryParamMap.subscribe(params => {
          this.token = params.get('token')
          if (this.token === game.whiteToken) {
            this.color = Color.White
          } else if (this.token === game.blackToken) {
            this.color = Color.Black
          }
        })
      })
    })
  }

  /**
   * Play move.
   * @param move Move.
   */
  play(move: Move): void {
    if (this.game !== null && this.token !== null) {
      if (move.type === MoveType.Promotion) {
        this.promotionMove = move
      } else {
        this.gameService.play(this.token, move).subscribe()
      }
    }
  }
}
