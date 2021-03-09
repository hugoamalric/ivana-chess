import {Injectable} from '@angular/core'
import {BehaviorSubject, Observable} from 'rxjs'
import {ApiErrorCode} from './api-error-code.enum'
import {HttpErrorResponse} from '@angular/common/http'
import {ApiError} from './api-error'

/**
 * Error service.
 */
@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  /**
   * Subject which contains error code.
   * @private
   */
  private code = new BehaviorSubject<ApiErrorCode | null>(null)

  /**
   * Initialize service.
   */
  constructor() {
  }

  /**
   * Get error code.
   *
   * @return Observable which contains error code.
   */
  errorCode(): Observable<ApiErrorCode | null> {
    return this.code
  }

  /**
   * Handle API error.
   *
   * @param errorResponse HTTP error response.
   */
  handleApiError(errorResponse: HttpErrorResponse): void {
    const error = errorResponse.error as ApiError
    this.code.next(error.code)
  }
}
