/**
 * API error code.
 */
export enum ApiErrorCode {
  /**
   * Bad credentials error code.
   */
  BadCredentials = 'bad_credentials',

  /**
   * Forbidden error code.
   */
  Forbidden = 'forbidden',

  /**
   * Game not found error code.
   */
  GameNotFound = 'game_not_found',

  /**
   * Invalid content type error code.
   */
  InvalidContentType = 'invalid_content_type',

  /**
   * Invalid move code.
   */
  InvalidMove = 'invalid_move',

  /**
   * Invalid parameter error code.
   */
  InvalidParameter = 'invalid_parameter',

  /**
   * Invalid player code.
   */
  InvalidPlayer = 'invalid_player',

  /**
   * Invalid request body error code.
   */
  InvalidRequestBody = 'invalid_request_body',

  /**
   * Method not allowed error code.
   */
  MethodNotAllowed = 'method_not_allowed',

  /**
   * Not found error code.
   */
  NotFound = 'not_found',

  /**
   * Player not found error code.
   */
  PlayerNotFound = 'player_not_found',

  /**
   * Players are same user code.
   */
  PlayersAreSameUser = 'players_are_same_user',

  /**
   * Unauthorized error code.
   */
  Unauthorized = 'unauthorized',

  /**
   * Unexpected error code.
   */
  UnexpectedError = 'unexpected_error',

  /**
   * User email already used error code.
   */
  UserEmailAlreadyUsed = 'email_already_used',

  /**
   * User pseudo already used error code.
   */
  UserPseudoAlreadyUsed = 'pseudo_already_used',

  /**
   * Validation error code.
   */
  ValidationError = 'validation_error',
}
