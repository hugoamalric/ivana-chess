import {Component, OnInit} from '@angular/core'
import {GameService} from '../game.service'
import {Page} from '../page'
import {ActivatedRoute, Router} from '@angular/router'
import {GameSummary} from '../game-summary'
import {faArrowLeft, faArrowRight} from '@fortawesome/free-solid-svg-icons'
import {User} from '../user'
import {AuthenticationService} from '../authentication.service'
import {catchError, finalize} from 'rxjs/operators'
import {ErrorService} from '../error.service'

/**
 * Home component.
 */
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  /**
   * Page size.
   */
  pageSize: number = 10

  /**
   * Page.
   */
  page: Page<GameSummary> | null = null

  /**
   * Next icon.
   */
  nextIcon = faArrowRight

  /**
   * Previous icon.
   */
  previousIcon = faArrowLeft

  /**
   * Current authenticated user.
   */
  me: User | null = null

  /**
   * True if previous/next page is pending, false otherwise.
   */
  pagePending: boolean = false

  /**
   * Initialize component.
   *
   * @param gameService Game service.
   * @param authService Authentication service.
   * @param errorService Error service.
   * @param route Current route.
   * @param router Router.
   */
  constructor(
    private gameService: GameService,
    private authService: AuthenticationService,
    private errorService: ErrorService,
    private route: ActivatedRoute,
    private router: Router
  ) {
  }

  /**
   * Fetch next page.
   */
  nextPage(): void {
    if (this.page) {
      this.fetchPage(this.page.number + 1)
    }
  }

  ngOnInit(): void {
    this.authService.me().subscribe(user => this.me = user)
    this.route.queryParamMap.subscribe(params => {
      const pageNb = params.get('page')
      if (pageNb === null) {
        this.router.navigate([], {
          queryParams: {
            page: 1
          }
        }).then()
      } else {
        this.fetchPage(Number(pageNb))
      }
    })
  }

  /**
   * Fetch previous page.
   */
  previousPage(): void {
    if (this.page) {
      this.fetchPage(this.page.number - 1)
    }
  }

  /**
   * Fetch page.
   * @param pageNb Page number.
   * @private
   */
  private fetchPage(pageNb: number): void {
    this.router.navigate([], {
      queryParams: {
        page: pageNb
      }
    }).then(() => {
      this.pagePending = true
      this.gameService.getAll(pageNb, this.pageSize)
        .pipe(
          catchError(error => this.errorService.handleApiError<Page<GameSummary>>(error)),
          finalize(() => this.pagePending = false)
        )
        .subscribe(page => this.page = page)
    })
  }
}
