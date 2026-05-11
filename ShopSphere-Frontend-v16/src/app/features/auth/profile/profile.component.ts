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
      email: [{value: '', disabled: true}], // Cannot change email
      address: [''],
      gender: ['']
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
      this.http.put(`${this.apiUrl}/update`, this.profileForm.getRawValue(), { headers: this.getHeaders() }).subscribe({
        next: () => {
          this.toast.showSuccess('Profile updated successfully!');
          this.isSaving = false;
        },
        error: () => {
          this.toast.showError('Could not update profile.');
          this.isSaving = false;
        }
      });
    }
  }
}