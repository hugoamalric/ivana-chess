import {Component, OnInit} from '@angular/core'
import {GameService} from '../game.service'

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  constructor(
    private gameService: GameService
  ) {
  }

  createNewGame(): void {
    this.gameService.createNewGame().subscribe()
  }

  ngOnInit(): void {
  }
}
