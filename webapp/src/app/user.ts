import {Role} from './role.enum'

/**
 * User.
 */
export interface User {
  /**
   * ID.
   */
  id: string

  /**
   * Pseudo.
   */
  pseudo: string

  /**
   * Email.
   */
  email: string | undefined

  /**
   * Creation date.
   */
  creationDate: Date

  /**
   * Role.
   */
  role: Role
}
