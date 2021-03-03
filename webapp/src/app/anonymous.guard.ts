import {Injectable} from '@angular/core'
import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree} from '@angular/router'
import {Observable} from 'rxjs'
import {AuthenticationService} from './authentication.service'
import {map, tap} from 'rxjs/operators'
import {HistoryService} from './history.service'

/**
 * Guard which ensure user is anonymous.
 */
@Injectable({
  providedIn: 'root'
})
export class AnonymousGuard implements CanActivate {
  /**
   * Initialize guard.
   *
   * @param authService Authentication service.
   * @param historyService History service.
   */
  constructor(
    private authService: AuthenticationService,
    private historyService: HistoryService
  ) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.authService.me()
      .pipe(
        map(user => user === null),
        tap(isAnonymous => {
          if (!isAnonymous) {
            this.historyService.goBack('/')
          }
        })
      )
  }
}
