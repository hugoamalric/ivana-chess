import {Injectable} from '@angular/core'
import {IvanaChessService} from './ivana-chess.service'
import {HttpClient} from '@angular/common/http'
import {Observable} from 'rxjs'
import {RxStompService} from '@stomp/ng2-stompjs'
import {User} from './user'
import {UserSubscription} from './user-subscription'
import {Page} from './page'

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
   * Check if user exists.
   *
   * @param email Email.
   * @return Observable which contains true if user exists, false otherwise.
   */
  existsByEmail(email: string): Observable<boolean> {
    return this.exists(this.path, 'email', email)
  }

  /**
   * Check if user exists.
   *
   * @param pseudo Pseudo.
   * @return Observable which contains true if user exists, false otherwise.
   */
  existsByPseudo(pseudo: string): Observable<boolean> {
    return this.exists(this.path, 'pseudo', pseudo)
  }

  /**
   * Search users by pseudo.
   *
   * @param q Part of pseudo to search.
   * @param fields List of fields in which search.
   * @param page Page number.
   * @param size Page size.
   * @param excluding Set of user UUIDs to exclude of the search.
   * @return Observable which contains page.
   */
  search(q: string, fields: string[], excluding: string[] = [], page: number = 1, size: number = 5): Observable<Page<User>> {
    return this.get<Page<User>>(
      `${this.path}/search`,
      {
        q,
        field: fields,
        page: page.toString(),
        size: size.toString(),
        exclude: excluding
      }
    )
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
