import {Component, OnInit} from '@angular/core'
import {ActivatedRoute} from '@angular/router'
import {GameService} from '../game.service'
import {Game} from '../game'
import {Location} from '@angular/common'
import {Color} from '../color.enum'

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
   * Player color.
   */
  color: Color | null = null

  /**
   * Initialize component.
   * @param gameService Game service.
   * @param route Current route.
   * @param location Current location.
   */
  constructor(
    private gameService: GameService,
    private route: ActivatedRoute,
    private location: Location
  ) {
  }

  /**
   * Go back.
   */
  goBack(): void {
    this.location.back()
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id')
      if (id === null) {
        console.error('id must be not null!')
      } else {
        this.gameService.getGame(id).subscribe(game => {
          this.game = game
          this.gameService.watchGame(this.game.id).subscribe(game => this.game = game)
          this.route.queryParamMap.subscribe(params => {
            const token = params.get('token')
            if (token === game.whiteToken) {
              this.color = Color.White
            } else if (token === game.blackToken) {
              this.color = Color.Black
            }
          })
        })
      }
    })
  }
}
