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
   * Delete an user.
   *
   * @param id User ID.
   * @return Observable<void> Empty observable.
   */
  delete(id: string): Observable<void> {
    return this.doDelete(`${this.path}/${id}`)
  }

  /**
   * Check if user exists with an email.
   *
   * @param email Email.
   * @param excluding List of user IDs to exclude from the search.
   * @return Observable<boolean> Observable which contains true if user exists, false otherwise.
   */
  existsWithEmail(email: string, excluding: string[] = []): Observable<boolean> {
    return this.existsWith(this.path, 'email', email, excluding)
  }

  /**
   * Check if user exists with a pseudo.
   *
   * @param pseudo Pseudo.
   * @return Observable<boolean> Observable which contains true if user exists, false otherwise.
   */
  existsWithPseudo(pseudo: string): Observable<boolean> {
    return this.existsWith(this.path, 'pseudo', pseudo)
  }

  /**
   * Get user.
   *
   * @param id User ID.
   * @return Observable<User> Observable which contains user.
   */
  get(id: string): Observable<User> {
    return this.doGet(`${this.path}/${id}`)
  }

  /**
   * Search users by pseudo.
   *
   * @param q Part of pseudo to search.
   * @param fields List of fields in which search.
   * @param page Page number.
   * @param size Page size.
   * @param excluding Set of user UUIDs to exclude of the search.
   * @return Observable<Page<User>> Observable which contains page.
   */
  search(q: string, fields: string[], excluding: string[] = [], page: number = 1, size: number = 5): Observable<Page<User>> {
    return this.doGet<Page<User>>(
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
   * Update authenticated user.
   *
   * @param email Email.
   * @param password Password.
   * @return Observable<User> Observable which contains updated user.
   */
  selfUpdate(email: string | null = null, password: string | null = null): Observable<User> {
    return this.doPut<User>(`${this.path}`, {email, password})
  }

  /**
   * Sign-up.
   *
   * @param userCreation User creation.
   * @return Observable<User> Observable which contains user.
   */
  signUp(userCreation: UserSubscription): Observable<User> {
    return this.doPost<User>(`${this.path}/signup`, userCreation)
  }
}
