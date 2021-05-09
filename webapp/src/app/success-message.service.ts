import {Injectable} from '@angular/core'
import {BehaviorSubject, Observable} from 'rxjs'
import {ApiErrorCode} from './api-error-code.enum'
import {SuccessMessageCode} from './success-message-code.enum'

/**
 * Success message service.
 */
@Injectable({
  providedIn: 'root'
})
export class SuccessMessageService {
  /**
   * Subject which contains success message code.
   */
  private _code = new BehaviorSubject<SuccessMessageCode | null>(null)

  /**
   * Initialize service.
   */
  constructor() {
  }

  /**
   * Get success message code.
   *
   * @return Observable<ApiErrorCode|null> Observable which contains success message code or null if no message.
   */
  get code(): Observable<SuccessMessageCode | null> {
    return this._code
  }

  /**
   * Send code.
   *
   * @param code Code.
   */
  sendCode(code: SuccessMessageCode): void {
    this._code.next(code)
  }
}
