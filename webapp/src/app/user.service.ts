import {Injectable} from '@angular/core'
import {IvanaChessService} from './ivana-chess.service'
import {HttpClient} from '@angular/common/http'
import {Observable} from 'rxjs'
import {RxStompService} from '@stomp/ng2-stompjs'
import {User} from './user'
import {UserSubscription} from './user-subscription'

/**
 * User service.
 */
@Injectable({
  providedIn: 'root'
})
export class UserService extends IvanaChessService {
  /**
   * Path.
   * @private
   */
  private path: string = '/user'

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
   * Search users by pseudo.
   *
   * @param q Part of pseudo to search.
   * @param maxSize Maximum size of returned list.
   * @return Observable which contains found users.
   */
  search(q: string, maxSize: number = 5): Observable<User[]> {
    return this.get<User[]>(`${this.path}/search`, {q, maxSize: maxSize.toString()})
  }

  /**
   * Sign-up.
   *
   * @param userCreation User creation.
   * @return User.
   */
  signUp(userCreation: UserSubscription): Observable<User> {
    return this.post<User>(`${this.path}/signup`, userCreation)
  }
}
