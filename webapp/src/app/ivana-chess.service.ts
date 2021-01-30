import {Injectable} from '@angular/core'
import {HttpClient} from '@angular/common/http'
import {environment} from '../environments/environment'
import {Observable} from 'rxjs'
import {Page} from './page'

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
   * @protected
   */
  protected constructor(
    protected http: HttpClient
  ) {
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
}
