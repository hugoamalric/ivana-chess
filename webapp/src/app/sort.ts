/**
 * Sort.
 */
import {SortOrder} from './sort-order'

export class Sort {
  /**
   * Initialize sort.
   * @param property Property name.
   * @param order Sort order.
   */
  constructor(
    public readonly property: string,
    public readonly order: SortOrder = SortOrder.Ascending,
  ) {
  }
}
