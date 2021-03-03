import {Injectable} from '@angular/core'
import {IvanaChessService} from './ivana-chess.service'
import {HttpClient} from '@angular/common/http'
import {Observable} from 'rxjs'
import {RxStompService} from '@stomp/ng2-stompjs'
import {User} from './user'
import {UserCreation} from './user-creation'

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
   * Sign-up.
   *
   * @param userCreation User creation.
   * @return User.
   */
  signUp(userCreation: UserCreation): Observable<User> {
    return this.post<User>(`${this.path}/signup`, userCreation)
  }
}
