/**
 * API error.
 */
import {ApiErrorCode} from './api-error-code.enum'

export interface ApiError {
  /**
   * Code.
   */
  code: ApiErrorCode
}
