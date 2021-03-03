import {Component, OnInit} from '@angular/core'
import {GameService} from '../game.service'
import {Page} from '../page'
import {ActivatedRoute, Router} from '@angular/router'
import {GameSummary} from '../game-summary'

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
   * Initialize component.
   * @param gameService Game service.
   * @param route Current route.
   * @param router Router.
   */
  constructor(
    private gameService: GameService,
    private route: ActivatedRoute,
    private router: Router
  ) {
  }

  /**
   * Create new game.
   */
  createNewGame(): void {
    this.gameService.createNewGame().subscribe(() => {
      if (this.page) {
        this.fetchPage(this.page.number)
      }
    })
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
    this.route.queryParamMap.subscribe(params => {
      const pageNb = params.get('page')
      if (pageNb === null) {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate([], {
          queryParams: {
            page: 1
          }
        })
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
    }).then(() => this.gameService.getAll(pageNb, this.pageSize).subscribe(page => this.page = page))
  }
}
