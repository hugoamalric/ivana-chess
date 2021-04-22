import {Injectable} from '@angular/core'
import {BehaviorSubject, Observable, of, throwError} from 'rxjs'
import {ApiErrorCode} from './api-error-code.enum'
import {HttpErrorResponse} from '@angular/common/http'
import {ApiError} from './api-error'
import {Router} from '@angular/router'

/**
 * Error service.
 */
@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  /**
   * Subject which contains error code.
   */
  private code = new BehaviorSubject<ApiErrorCode | null>(null)

  /**
   * Initialize service.
   *
   * @param router Router.
   */
  constructor(
    private router: Router
  ) {
  }

  /**
   * Get error code.
   *
   * @return Observable<ApiErrorCode|null> Observable which contains error code or null if no error.
   */
  errorCode(): Observable<ApiErrorCode | null> {
    return this.code
  }

  /**
   * Handle API error.
   *
   * If default value is not specified, the error is propagated.
   *
   * @param errorResponse HTTP error response.
   * @param def Default value.
   * @return Observable<T> Observable which contains default value if it is specified, the error otherwise.
   */
  handleApiError<T>(errorResponse: HttpErrorResponse, def: T | undefined = undefined): Observable<T> {
    if (errorResponse.status === 0 || errorResponse.status === 502 || errorResponse.status === 503) {
      this.router.navigate(['/error'], {
        queryParams: {
          type: 'unavailable'
        }
      }).then()
    } else {
      const error = errorResponse.error as ApiError
      switch (error.code) {
        case ApiErrorCode.Forbidden:
          this.router.navigate(['/error'], {
            queryParams: {
              type: 'forbidden'
            }
          }).then()
          break
        case ApiErrorCode.Unauthorized:
          this.router.navigate(['/login']).then()
          break
        default:
          this.code.next(error.code)
      }
    }
    if (def === undefined) {
      return throwError(errorResponse)
    } else {
      return of(def)
    }
  }
}
