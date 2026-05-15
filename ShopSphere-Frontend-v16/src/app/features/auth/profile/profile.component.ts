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
  private apiUrl = 'http://localhost:9090/api/auth/profile'; 

  constructor(private fb: FormBuilder, private http: HttpClient, private toast: ToastService) {}

  ngOnInit(): void {
    this.profileForm = this.fb.group({
      name: ['', Validators.required],
      username: [{value: '', disabled: true}], // FIXED: Added separate username field (Locked)
      email: [{value: '', disabled: true}],    // FIXED: email stays Locked
      address: [''],
      gender: [''],
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
          username: user.username, // FIXED: Now mapping the separate username field
          email: user.email,
          address: user.address,
          gender: user.gender
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
      const payload = this.profileForm.getRawValue();

      if (!payload.password || payload.password.trim() === '') {
        delete payload.password;
      }

      this.http.put(`${this.apiUrl}/update`, payload, { headers: this.getHeaders() }).subscribe({
        next: () => {
          this.toast.showSuccess('Profile updated successfully!');
          this.isSaving = false;
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