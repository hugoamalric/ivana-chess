import {Component, OnInit} from '@angular/core'
import {AbstractControl, FormControl, FormGroup, Validators} from '@angular/forms'
import {Observable, of, throwError} from 'rxjs'
import {catchError, debounceTime, distinctUntilChanged, finalize, switchMap, tap} from 'rxjs/operators'
import {UserService} from '../user.service'
import {User} from '../user'
import {GameService} from '../game.service'
import {AuthenticationService} from '../authentication.service'
import {Router} from '@angular/router'
import {NgbTypeaheadSelectItemEvent} from '@ng-bootstrap/ng-bootstrap'
import {ErrorService} from '../error.service'

/**
 * New game component.
 */
@Component({
  selector: 'app-new-game',
  templateUrl: './new-game.component.html',
  styleUrls: ['./new-game.component.scss']
})
export class NewGameComponent implements OnInit {
  /**
   * New game form.
   */
  newGameForm = new FormGroup({
    blackPlayer: new FormControl('', Validators.required)
  })

  /**
   * Selected black player.
   * @private
   */
  selectedBlackPayer: User | null = null

  /**
   * True if user is searching black player, false otherwise.
   */
  searchingForBlackPlayer: boolean = false

  /**
   * Authenticated user.
   */
  me: User | null = null

  /**
   * True if game is creating, false otherwise.
   */
  creating: boolean = false

  /**
   * Function which search users for black player.
   *
   * @param text Observable which contains field text.
   * @return Observable which contains found users.
   */
  searchBlackPlayer = (text: Observable<string>) => text.pipe(
    debounceTime(300),
    distinctUntilChanged(),
    tap(() => {
      this.searchingForBlackPlayer = true
      this.selectedBlackPayer = null
    }),
    switchMap(q => this.userService.search(q)),
    catchError(error => {
      this.errorService.handleApiError(error)
      return of([])
    }),
    finalize(() => this.searchingForBlackPlayer = false)
  )

  /**
   * Function which format found users.
   *
   * @param user User.
   * @return User pseudo.
   */
  userFormatter = (user: User) => user.pseudo

  /**
   * Initialize component.
   *
   * @param gameService Game service.
   * @param userService User service.
   * @param authService Authentication service.
   * @param errorService Error service.
   * @param router Router.
   */
  constructor(
    private gameService: GameService,
    private userService: UserService,
    private authService: AuthenticationService,
    private errorService: ErrorService,
    private router: Router
  ) {
  }

  /**
   * Get black player form control.
   */
  get blackPlayer(): AbstractControl {
    return this.newGameForm.get('blackPlayer')!!
  }

  /**
   * Create new game.
   */
  create(): void {
    this.creating = true
    this.gameService.createNewGame(this.me!!.id, this.selectedBlackPayer!!.id)
      .pipe(
        catchError(error => {
          this.errorService.handleApiError(error)
          return throwError(error)
        }),
        finalize(() => this.creating = false)
      )
      .subscribe(game => this.router.navigate(['/game', game.id]))
  }

  ngOnInit(): void {
    this.authService.me().subscribe(user => this.me = user)
  }

  /**
   * Select black player.
   *
   * @param event Select event.
   */
  selectBlackPlayer(event: NgbTypeaheadSelectItemEvent<User>): void {
    this.selectedBlackPayer = event.item
  }
}
