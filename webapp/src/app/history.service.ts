import {Injectable} from '@angular/core'
import {NavigationEnd, Router} from '@angular/router'
import {filter} from 'rxjs/operators'

/**
 * History service.
 */
@Injectable({
  providedIn: 'root'
})
export class HistoryService {
  /**
   * List of visited URI sorted from oldest to newest.
   * @private
   */
  private history: string[] = []

  /**
   * Initialize service.
   *
   * @param router Router.
   */
  constructor(
    private router: Router
  ) {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(event => this.history.push((event as NavigationEnd).url))
  }

  /**
   * Go back.
   *
   * @param defaultUrl If history is empty.
   */
  goBack(defaultUrl: string): void {
    let url: string
    if (this.history.length <= 1) {
      url = defaultUrl
    } else {
      url = this.history[this.history.length - 2]
    }
    this.router.navigateByUrl(url).then(
      () => {
        const lastHistoryEntry = this.history.pop()
        this.history.pop()
        this.history[this.history.length - 1] = lastHistoryEntry!!
      }
    )
  }
}
