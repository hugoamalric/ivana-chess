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
import {Sort} from '../sort'
import {SortOrder} from '../sort-order'
import {Filter} from '../filter'
import {GameState} from '../game-state.enum'

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
   * Now.
   */
  private readonly now = new Date()

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
   * Compute the number of minutes between date and now.
   *
   * @param iso8601Date Date in 8601 format.
   * @return number Number of minutes between the date and now.
   */
  since(iso8601Date: string): number {
    const date = new Date(iso8601Date)
    return Math.floor((this.now.getTime() - date.getTime()) / 1000 / 60)
  }

  /**
   * Fetch page.
   *
   * @param pageNb Page number.
   */
  private fetchPage(pageNb: number): void {
    this.router.navigate([], {
      queryParams: {
        page: pageNb
      }
    }).then(() => {
      this.pagePending = true
      this.gameService.getPage(
        pageNb,
        this.pageSize,
        [new Sort('creationDate', SortOrder.Descending), new Sort('id', SortOrder.Ascending)],
        [new Filter('state', GameState.InGame)],
      )
        .pipe(
          catchError(error => this.errorService.handleApiError<Page<GameSummary>>(error)),
          finalize(() => this.pagePending = false)
        )
        .subscribe(page => this.page = page)
    })
  }
}
