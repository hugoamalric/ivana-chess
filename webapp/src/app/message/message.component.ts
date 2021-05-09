import {Component, OnInit} from '@angular/core'
import {ApiErrorCode} from '../api-error-code.enum'
import {ErrorService} from '../error.service'
import {SuccessMessageService} from '../success-message.service'
import {SuccessMessageCode} from '../success-message-code.enum'

/**
 * Message component.
 */
@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {
  /**
   * Message code.
   */
  code: string | null = null

  /**
   * Type of message.
   */
  type: 'danger' | 'success' | null = null

  /**
   * API error code enumeration.
   */
  ApiErrorCode = ApiErrorCode

  /**
   * Success message code enumeration.
   */
  SuccessMessageCode = SuccessMessageCode

  /**
   * Initialize component.
   *
   * @param errorService Error service.
   * @param successMessageService Success message service.
   */
  constructor(
    private errorService: ErrorService,
    private successMessageService: SuccessMessageService,
  ) {
  }

  /**
   * Close alert.
   */
  close(): void {
    this.code = null
    this.type = null
  }

  /**
   * Return if current error is an error made by a developer.
   *
   * @return boolean True if current error is made by a developer, false otherwise.
   */
  isDevError(): boolean {
    return this.code === ApiErrorCode.InvalidContentType ||
      this.code === ApiErrorCode.InvalidParameter ||
      this.code === ApiErrorCode.InvalidRequestBody ||
      this.code === ApiErrorCode.MethodNotAllowed ||
      this.code === ApiErrorCode.NotFound ||
      this.code === ApiErrorCode.UnexpectedError ||
      this.code === ApiErrorCode.ValidationError
  }

  ngOnInit(): void {
    this.errorService.code.subscribe(code => {
      this.code = code
      this.type = 'danger'
    })
    this.successMessageService.code.subscribe(code => {
      this.code = code
      this.type = 'success'
    })
  }
}
