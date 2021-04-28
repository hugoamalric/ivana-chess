/**
 * Filter.
 */
export class Filter {
  /**
   * Initialize filter.
   *
   * @param field Field name.
   * @param value Value.
   */
  constructor(
    public readonly field: string,
    public readonly value: any,
  ) {
  }
}
