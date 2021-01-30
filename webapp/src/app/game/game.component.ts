import {Component, OnInit} from '@angular/core'
import {ActivatedRoute} from '@angular/router'
import {GameService} from '../game.service'
import {Game} from '../game'

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
   * Initialize component.
   * @param gameService Game service.
   * @param route Current route.
   */
  constructor(
    private gameService: GameService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id')
      if (id === null) {
        console.error('id must be not null!')
      } else {
        this.gameService.getGame(id).subscribe(game => this.game = game)
      }
    })
  }

}
