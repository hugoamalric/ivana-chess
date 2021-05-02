import {Component, OnInit} from '@angular/core'
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap'

/**
 * Confirm modal component.
 */
@Component({
  selector: 'app-confirm-modal',
  templateUrl: './confirm-modal.component.html',
  styleUrls: ['./confirm-modal.component.scss']
})
export class ConfirmModalComponent implements OnInit {
  /**
   * Initialize component.
   *
   * @param modal Active modal.
   */
  constructor(
    private modal: NgbActiveModal
  ) {
  }

  /**
   * Cancel action.
   */
  cancel(): void {
    this.modal.close(false)
  }

  /**
   * Confirm action.
   */
  confirm(): void {
    this.modal.close(true)
  }

  ngOnInit(): void {
  }

}
