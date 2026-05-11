import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'] 
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
      
      // FIX: Spring Boot expects "username" and "password". 
      // We map the form's email input to the 'username' key.
      const payload = {
        username: this.loginForm.value.email,
        password: this.loginForm.value.password
      };
      
      this.authService.login(payload).subscribe({
        next: () => {
          const role = this.authService.getUserRole();
          this.isLoading = false;
          
          if (role === 'ROLE_ADMIN') {
            this.router.navigate(['/analytics/dashboard']);
          } else if (role === 'ROLE_ARTISAN') {
            this.router.navigate(['/artisan/dashboard']);
          } else if (role === 'ROLE_LOGISTICS') {
            this.router.navigate(['/logistics/queue']);
          } else {
            this.router.navigate(['/']); 
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