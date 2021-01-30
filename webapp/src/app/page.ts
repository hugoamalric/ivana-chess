/**
 * Page.
 */
export interface Page<T> {
  /**
   * Content.
   */
  content: T[]

  /**
   * Current number.
   */
  number: number

  /**
   * Total number of elements.
   */
  totalItems: number

  /**
   * Total number of pages.
   */
  totalPages: number
}
