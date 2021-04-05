import {Component, HostListener, OnDestroy, OnInit} from '@angular/core'
import {GameService} from '../game.service'
import {ErrorService} from '../error.service'
import {catchError} from 'rxjs/operators'
import {Router} from '@angular/router'
import {Game} from '../game'
import {AuthenticationService} from '../authentication.service'

@Component({
  selector: 'app-matchmaking',
  templateUrl: './matchmaking.component.html',
  styleUrls: ['./matchmaking.component.scss']
})
export class MatchmakingComponent implements OnDestroy, OnInit {
  /**
   * Initialize component.
   *
   * @param gameService Game service.
   * @param authService Authentication service.
   * @param errorService Error service.
   * @param router Router.
   */
  constructor(
    private gameService: GameService,
    private authService: AuthenticationService,
    private errorService: ErrorService,
    private router: Router,
  ) {
  }

  /**
   * Remove current authenticated user from matchmaking queue.
   */
  @HostListener('window:beforeunload')
  leaveMatchmakingQueue(): void {
    this.gameService.leaveMatchmakingQueue()
      .pipe(catchError(error => this.errorService.handleApiError(error)))
      .subscribe(() => this.router.navigate(['/']))
  }

  ngOnDestroy(): void {
    this.gameService.leaveMatchmakingQueue()
      .pipe(catchError(error => this.errorService.handleApiError(error)))
  }

  ngOnInit(): void {
    this.authService.me().subscribe(user => {
      this.gameService.joinMatchmakingQueue()
        .pipe(catchError(error => this.errorService.handleApiError(error)))
        .subscribe(() =>
          this.gameService.watchMatchmakingQueue()
            .pipe(catchError(error => this.errorService.handleApiError<Game>(error)))
            .subscribe(game => {
              if (game.whitePlayer.id === user!!.id || game.blackPlayer.id === user!!.id) {
                this.router.navigate(['/game', game.id]).then()
              }
            })
        )
    })
  }
}
