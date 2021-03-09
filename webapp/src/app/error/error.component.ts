import {Component, OnInit} from '@angular/core'
import {ApiErrorCode} from '../api-error-code.enum'
import {ErrorService} from '../error.service'

/**
 * Error message component.
 */
@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.scss']
})
export class ErrorComponent implements OnInit {
  errorCode: ApiErrorCode | null = null

  /**
   * API error code enumeration.
   */
  ApiErrorCode = ApiErrorCode

  /**
   * Initialize component.
   *
   * @param errorService Error service.
   */
  constructor(
    private errorService: ErrorService
  ) {
  }

  /**
   * Close alert.
   */
  close(): void {
    this.errorCode = null
  }

  /**
   * Return if current error is an error made by a developer.
   *
   * @return True if current error is made by a developer, false otherwise.
   */
  isDevError(): boolean {
    return this.errorCode === ApiErrorCode.InvalidContentType ||
      this.errorCode === ApiErrorCode.InvalidParameter ||
      this.errorCode === ApiErrorCode.InvalidRequestBody ||
      this.errorCode === ApiErrorCode.MethodNotAllowed ||
      this.errorCode === ApiErrorCode.NotFound ||
      this.errorCode === ApiErrorCode.UnexpectedError ||
      this.errorCode === ApiErrorCode.ValidationError
  }

  ngOnInit(): void {
    this.errorService.errorCode().subscribe(errorCode => this.errorCode = errorCode)
  }
}
