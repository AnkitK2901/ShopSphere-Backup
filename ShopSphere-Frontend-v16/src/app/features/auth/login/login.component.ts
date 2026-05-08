import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'] // Isolated CSS!
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      
      this.authService.login(this.loginForm.value).subscribe({
        next: () => {
          const role = this.authService.getUserRole();
          this.isLoading = false;
          
          // The "Elevator" Routing Logic
          if (role === 'ROLE_ADMIN') {
            this.router.navigate(['/analytics/dashboard']);
          } else if (role === 'ROLE_ARTISAN') {
            this.router.navigate(['/artisan/dashboard']);
          } else if (role === 'ROLE_LOGISTICS') {
            this.router.navigate(['/logistics/queue']);
          } else {
            this.router.navigate(['/']); // Customers go to Catalog
          }
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = 'Invalid email or password. Please try again.';
          console.error(err);
        }
      });
    }
  }
}