import {Injectable} from '@angular/core'
import {HttpClient} from '@angular/common/http'
import {environment} from '../environments/environment'
import {Observable} from 'rxjs'
import {Page} from './page'
import {RxStompService} from '@stomp/ng2-stompjs'
import {map} from 'rxjs/operators'
import {Sort} from './sort'
import {SortOrder} from './sort-order'
import {Filter} from './filter'
import {Entity} from './entity'

/**
 * Ivana Chess service.
 */
@Injectable({
  providedIn: 'root'
})
export abstract class IvanaChessService {
  /**
   * Initialize service.
   *
   * @param http HTTP client.
   * @param stompService Stomp service.
   */
  protected constructor(
    private http: HttpClient,
    private stompService: RxStompService,
  ) {
  }

  /**
   * Execute DELETE request.
   *
   * @param uri URI.
   * @return Observable<void> Empty observable.
   */
  protected doDelete(uri: string): Observable<void> {
    return this.http.delete<void>(`${environment.apiBaseUrl}${uri}`, {withCredentials: true})
  }

  /**
   * Check if entity exists with specific property value.
   *
   * @param uri URI.
   * @param property Name of property.
   * @param value Value.
   * @param excluding List of user IDs to exclude from the search.
   * @return Observable Observable which contains true if entity exists, false otherwise.
   */
  protected existsWith(uri: string, property: string, value: any, excluding: string[] = []): Observable<boolean> {
    return this.doPaginatedGet<Entity>(uri, 1, 1, [], [new Filter(property, value)])
      .pipe(
        map(page =>
          page.totalItems > 0 && page.content.filter(entity => excluding.indexOf(entity.id) === -1).length === page.content.length
        )
      )
  }

  /**
   * Execute GET request.
   *
   * @param uri URI.
   * @param queryParams Query parameters.
   * @return Observable<T> Observable which contains response body.
   */
  protected doGet<T>(uri: string, queryParams: { [param: string]: string | string[] } = {}): Observable<T> {
    return this.http.get<T>(
      `${environment.apiBaseUrl}${uri}`,
      {
        withCredentials: true,
        params: queryParams
      }
    )
  }

  /**
   * Execute GET which returns page.
   *
   * @param uri URI.
   * @param page Page number.
   * @param size Page size.
   * @param sorts List of sorts.
   * @param filters List of filters.
   * @return Observable<T> Observable which contains page.
   */
  protected doPaginatedGet<T>(uri: string, page: number, size: number, sorts: Sort[] = [], filters: Filter[] = []): Observable<Page<T>> {
    const sortParams = sorts.map(sort => sort.order === SortOrder.Descending ? `-${sort.property}` : sort.property)
    const filterParams = filters.map(filter => `${filter.field}:${filter.value}`)
    return this.http.get<Page<T>>(
      `${environment.apiBaseUrl}${uri}`,
      {
        params: {
          page: page.toString(),
          size: size.toString(),
          sort: sortParams,
          filter: filterParams,
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
  protected doPost<T>(uri: string, body: any = null): Observable<T> {
    return this.http.post<T>(`${environment.apiBaseUrl}${uri}`, body, {withCredentials: true})
  }

  /**
   * Execute PUT request.
   *
   * @param uri URI.
   * @param body Request body.
   * @return Observable<T> Observable which contains response body.
   */
  protected doPut<T>(uri: string, body: any = null): Observable<T> {
    return this.http.put<T>(`${environment.apiBaseUrl}${uri}`, body, {withCredentials: true})
  }

  /**
   * Watch resource from web socket.
   *
   * @param uri URI.
   * @return Observable<T> Observable which contains resource.
   */
  protected doWatch<T>(uri: string): Observable<T> {
    return this.stompService.watch(`/topic${uri}`)
      .pipe(map(message => JSON.parse(message.body) as T))
  }
}
