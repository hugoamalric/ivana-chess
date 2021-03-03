/**
 * API error code.
 */
export enum ApiErrorCode {
  /**
   * Email already used.
   */
  EmailAlreadyUsed = 'email_already_used',

  /**
   * Pseudo already used.
   */
  PseudoAlreadyUsed = 'pseudo_already_used',

  /**
   * Unauthorized.
   */
  Unauthorized = 'unauthorized',

  /**
   * Unknown.
   */
  Unknown = 'unknown'
}
