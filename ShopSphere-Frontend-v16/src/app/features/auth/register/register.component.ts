import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['ROLE_CUSTOMER', Validators.required] 
    });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isLoading = true;
      
      const payload = {
        ...this.registerForm.value,
        username: this.registerForm.value.email 
      };

      this.authService.register(payload).subscribe({
        next: (response: any) => {
          // UPDATED: Now showing the specific message from the backend DTO
          this.toastService.showSuccess(response.message || 'Registration successful!');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error(err);
          // Backend now sends specific error strings (e.g., "Username is already taken")
          this.toastService.showError(err.error || 'Registration failed. Email might already be in use.');
          this.isLoading = false;
        }
      });
    }
  }
}