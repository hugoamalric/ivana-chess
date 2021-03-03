import {HttpErrorResponse} from '@angular/common/http'
import {Router} from '@angular/router'

/**
 * Handle API error.
 *
 * @param error Error.
 * @param router Router.
 */
export function handleApiError(error: HttpErrorResponse, router: Router) {
  console.log(error)
}
