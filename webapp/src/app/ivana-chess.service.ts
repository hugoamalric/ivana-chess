import {Injectable} from '@angular/core'
import {HttpClient} from '@angular/common/http'
import {environment} from '../environments/environment'
import {Observable} from 'rxjs'
import {Page} from './page'
import {RxStompService} from '@stomp/ng2-stompjs'
import {map} from 'rxjs/operators'
import {Exists} from './exists'
import {Sort} from './sort'
import {SortOrder} from './sort-order'

/**
 * Ivana Chess service.
 */
@Injectable({
  providedIn: 'root'
})
export abstract class IvanaChessService {
  /**
   * Initialize service.
   * @param http HTTP client.
   * @param stompService Stomp service.
   */
  protected constructor(
    private http: HttpClient,
    private stompService: RxStompService
  ) {
  }

  /**
   * Execute DELETE request.
   *
   * @param uri URI.
   * @return Observable<void> Empty observable.
   */
  protected delete(uri: string): Observable<void> {
    return this.http.delete<void>(`${environment.apiBaseUrl}${uri}`, {withCredentials: true})
  }

  /**
   * Check if an entity exists.
   *
   * @param uri URI.
   * @param by Field used to search user.
   * @param value Value of the field to search.
   * @return Observable<boolean> Observable which contains true if entity exists, false otherwise.
   */
  protected exists(uri: string, by: string, value: string): Observable<boolean> {
    return this.http.get<Exists>(
      `${environment.apiBaseUrl}${uri}/exists`,
      {
        withCredentials: true,
        params: {by, value}
      }
    )
      .pipe(map(dto => dto.exists))
  }

  /**
   * Execute GET request.
   *
   * @param uri URI.
   * @param queryParams Query parameters.
   * @return Observable<T> Observable which contains response body.
   */
  protected get<T>(uri: string, queryParams: { [param: string]: string | string[] } = {}): Observable<T> {
    return this.http.get<T>(
      `${environment.apiBaseUrl}${uri}`,
      {
        withCredentials: true,
        params: queryParams
      }
    )
  }

  /**
   * Get page.
   *
   * @param uri URI.
   * @param page Page number.
   * @param size Page size.
   * @param sorts List of sorts.
   * @return Observable<T> Observable which contains page.
   */
  protected getPage<T>(uri: string, page: number, size: number, sorts: Sort[] = []): Observable<Page<T>> {
    const sortParams = sorts.map(sort => sort.order === SortOrder.Descending ? `-${sort.property}` : sort.property)
    return this.http.get<Page<T>>(
      `${environment.apiBaseUrl}${uri}`,
      {
        params: {
          page: page.toString(),
          size: size.toString(),
          sort: sortParams,
        },
        withCredentials: true,
      }
    )
  }

  /**
   * Execute POST request.
   *
   * @param uri URI.
   * @param body Request body.
   * @return Observable<T> Observable which contains response body.
   */
  protected post<T>(uri: string, body: any = null): Observable<T> {
    return this.http.post<T>(`${environment.apiBaseUrl}${uri}`, body, {withCredentials: true})
  }

  /**
   * Execute PUT request.
   *
   * @param uri URI.
   * @param body Request body.
   * @return Observable<T> Observable which contains response body.
   */
  protected put<T>(uri: string, body: any = null): Observable<T> {
    return this.http.put<T>(`${environment.apiBaseUrl}${uri}`, body, {withCredentials: true})
  }

  /**
   * Watch resource from web socket.
   *
   * @param uri URI.
   * @return Observable<T> Observable which contains resource.
   */
  protected watch<T>(uri: string): Observable<T> {
    return this.stompService.watch(`/topic${uri}`)
      .pipe(map(message => JSON.parse(message.body) as T))
  }
}
