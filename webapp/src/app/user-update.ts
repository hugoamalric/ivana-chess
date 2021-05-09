/**
 * User update.
 */
export interface UserUpdate {
  /**
   * Email.
   */
  email: string

  /**
   * Password.
   */
  password: string

  /**
   * Role.
   */
  role: string | undefined
}
