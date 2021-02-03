import {Injectable} from '@angular/core'
import {HttpClient} from '@angular/common/http'
import {environment} from '../environments/environment'
import {Observable} from 'rxjs'
import {Page} from './page'
import {RxStompService} from '@stomp/ng2-stompjs'
import {map} from 'rxjs/operators'

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
   * @protected
   */
  protected constructor(
    private http: HttpClient,
    private stompService: RxStompService
  ) {
  }

  /**
   * Execute GET request.
   * @param uri URI.
   * @return Response body.
   * @protected
   */
  protected get<T>(uri: string): Observable<T> {
    return this.http.get<T>(`${environment.apiBaseUrl}${uri}`)
  }

  /**
   * Execute GET paginated request.
   * @param uri URI.
   * @param page Page number.
   * @param size Page size.
   * @return Page.
   * @protected
   */
  protected getPaginated<T>(uri: string, page: number, size: number): Observable<Page<T>> {
    return this.http.get<Page<T>>(`${environment.apiBaseUrl}${uri}`, {
      params: {
        page: page.toString(),
        size: size.toString()
      }
    })
  }

  /**
   * Execute POST request.
   * @param uri URI.
   * @param body Request body.
   * @return Response body.
   * @protected
   */
  protected post<T>(uri: string, body: any = null): Observable<T> {
    return this.http.post<T>(`${environment.apiBaseUrl}${uri}`, body)
  }

  /**
   * Execute PUT request.
   * @param uri URI.
   * @param body Request body.
   * @return Response body.
   * @protected
   */
  protected put<T>(uri: string, body: any = null): Observable<T> {
    return this.http.put<T>(`${environment.apiBaseUrl}${uri}`, body)
  }

  /**
   * Watch resource from web socket.
   * @param uri URI.
   * @return Resource.
   * @protected
   */
  protected watch<T>(uri: string): Observable<T> {
    return this.stompService.watch(`/topic${uri}`)
      .pipe(map(message => JSON.parse(message.body) as T))
  }
}
