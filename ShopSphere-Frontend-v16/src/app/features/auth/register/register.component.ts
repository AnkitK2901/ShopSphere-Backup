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
      // Added the dedicated username field here
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['ROLE_CUSTOMER', Validators.required] 
    });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isLoading = true;
      
      // Since username is now natively in the form, we can just pass the raw values
      const payload = this.registerForm.value;

      this.authService.register(payload).subscribe({
        next: (response: any) => {
          this.toastService.showSuccess(response.message || 'Registration successful!');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error(err);
          this.toastService.showError(err.error || 'Registration failed. Email or Username might already be in use.');
          this.isLoading = false;
        }
      });
    }
  }
}