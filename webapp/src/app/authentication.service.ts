import {Injectable} from '@angular/core'
import {IvanaChessService} from './ivana-chess.service'
import {HttpClient} from '@angular/common/http'
import {BehaviorSubject, Observable, of} from 'rxjs'
import {RxStompService} from '@stomp/ng2-stompjs'
import {User} from './user'
import {catchError, mergeMap, tap} from 'rxjs/operators'
import {Credentials} from './credentials'

/**
 * Authentication service.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthenticationService extends IvanaChessService {
  /**
   * Path.
   * @private
   */
  private path: string = '/auth'

  /**
   * Current authenticated user.
   * @private
   */
  private user: BehaviorSubject<User | null> | null = null

  /**
   * Initialize service.
   *
   * @param http HTTP client.
   * @param stompService Stomp service.
   */
  constructor(
    http: HttpClient,
    stompService: RxStompService
  ) {
    super(http, stompService)
  }

  /**
   * Generate JWT for user.
   *
   * @param creds User credentials.
   * @return User.
   */
  logIn(creds: Credentials): Observable<User | null> {
    return this.post(this.path, creds)
      .pipe(mergeMap(() => this.me()))
  }

  /**
   * Get current authenticated user.
   *
   * @return Current authenticated user or null if user is anonymous.
   */
  me(): Observable<User | null> {
    if (this.user !== null) {
      return this.user
    } else {
      return this.get<User>(this.path)
        .pipe(
          catchError(() => of(null)),
          tap(user => this.user = new BehaviorSubject(user)),
          mergeMap(() => this.user!!)
        )
    }
  }
}
