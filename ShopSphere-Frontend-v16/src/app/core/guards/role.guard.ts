import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service'; // THE FIX: Import ToastService

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  
  // THE FIX: Inject ToastService into the constructor
  constructor(
    private authService: AuthService, 
    private router: Router,
    private toastService: ToastService 
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const expectedRole = route.data['expectedRole'];
    const currentRole = this.authService.getUserRole();

    const normalizedExpected = expectedRole ? expectedRole.replace('ROLE_', '') : '';
    const normalizedCurrent = currentRole ? currentRole.replace('ROLE_', '') : '';

    if (this.authService.hasToken() && normalizedCurrent === normalizedExpected) {
      return true;
    }

    // THE FIX: Use Toast instead of browser alert popup
    this.toastService.showError('Access Denied: Role Mismatch.');
    this.router.navigate(['/']); 
    return false;
  }
}