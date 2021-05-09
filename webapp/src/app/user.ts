import {Role} from './role.enum'
import {Entity} from './entity'

/**
 * User.
 */
export interface User extends Entity {
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
