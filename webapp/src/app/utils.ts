import {HttpErrorResponse} from '@angular/common/http'
import {Router} from '@angular/router'
import {ApiError} from './api-error'
import {ApiErrorCode} from './api-error-code.enum'

/**
 * Handle API error.
 *
 * @param errorResponse Error.
 * @param router Router.
 */
export function handleApiError(errorResponse: HttpErrorResponse, router: Router): void {
  const error = errorResponse.error as ApiError
  switch (error.code) {
    case ApiErrorCode.Unauthorized:
      router.navigate(['/login']).then()
      break
    default:
      console.error(error)
      break
  }
}
