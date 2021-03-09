import {Component, OnInit} from '@angular/core'
import {ActivatedRoute} from '@angular/router'

/**
 * Error page component.
 */
@Component({
  selector: 'app-error-page',
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.scss']
})
export class ErrorPageComponent implements OnInit {
  /**
   * Error type.
   */
  errorType: string | null = null

  /**
   * Initialize component.
   *
   * @param route Current route.
   */
  constructor(
    private route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(queryParams => this.errorType = queryParams.get('type'))
  }

}
