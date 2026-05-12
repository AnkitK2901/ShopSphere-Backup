import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profileForm!: FormGroup;
  isLoading = true;
  isSaving = false;
  private apiUrl = 'http://localhost:9090/api/auth/profile'; // adjust if needed

  constructor(private fb: FormBuilder, private http: HttpClient, private toast: ToastService) {}

  ngOnInit(): void {
    this.profileForm = this.fb.group({
      name: ['', Validators.required],
      email: [{value: '', disabled: true}], // Cannot change email
      address: [''],
      gender: [''],
      // ADDED: Optional password field for updating
      password: [''] 
    });
    this.loadProfile();
  }

  private getHeaders() {
    return new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('jwt_token')}`);
  }

  loadProfile() {
    this.http.get<any>(`${this.apiUrl}/me`, { headers: this.getHeaders() }).subscribe({
      next: (user) => {
        this.profileForm.patchValue({
          name: user.name,
          email: user.email,
          address: user.address,
          gender: user.gender
          // Note: We purposely do NOT patch the password here! It stays blank.
        });
        this.isLoading = false;
      },
      error: () => {
        this.toast.showError('Failed to load profile.');
        this.isLoading = false;
      }
    });
  }

  saveProfile() {
    if (this.profileForm.valid) {
      this.isSaving = true;
      
      // 1. Extract the raw values from the form
      const payload = this.profileForm.getRawValue();

      // 2. THE FIX: If the password field is empty, delete it from the payload
      // so the backend completely ignores it and keeps the old password safe.
      if (!payload.password || payload.password.trim() === '') {
        delete payload.password;
      }

      this.http.put(`${this.apiUrl}/update`, payload, { headers: this.getHeaders() }).subscribe({
        next: () => {
          this.toast.showSuccess('Profile updated successfully!');
          this.isSaving = false;
          
          // Clear the password field after a successful save
          this.profileForm.get('password')?.setValue('');
        },
        error: () => {
          this.toast.showError('Could not update profile.');
          this.isSaving = false;
        }
      });
    }
  }
}