import {Component, OnInit} from '@angular/core'
import {FormControl, FormGroup, Validators} from '@angular/forms'
import {Credentials} from '../credentials'
import {AuthenticationService} from '../authentication.service'
import {handleApiError} from '../utils'
import {HttpErrorResponse} from '@angular/common/http'
import {Router} from '@angular/router'

/**
 * Log-in component.
 */
@Component({
  selector: 'app-login',
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.scss']
})
export class LogInComponent implements OnInit {
  /**
   * Log-in form.
   */
  logInForm = new FormGroup({
    pseudo: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required)
  })

  /**
   * Error message.
   */
  errorMessage: string | null = null

  /**
   * Initialize component.
   *
   * @param authService Authentication service.
   * @param router Router.
   */
  constructor(
    private authService: AuthenticationService,
    private router: Router
  ) {
  }

  /**
   * Submit log-in form.
   */
  logIn(): void {
    const creds = this.logInForm.value as Credentials
    this.authService.logIn(creds).subscribe(
      () => this.router.navigate(['/']),
      (error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.errorMessage = 'Invalid pseudo or password.'
        } else {
          handleApiError(error, this.router)
        }
      }
    )
  }

  ngOnInit(): void {
  }

}
