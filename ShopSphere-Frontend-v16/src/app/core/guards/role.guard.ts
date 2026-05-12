import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const expectedRole = route.data['expectedRole'];
    const currentRole = this.authService.getUserRole();

    // FIX: Strip 'ROLE_' from both to ensure a perfect match every time
    const normalizedExpected = expectedRole ? expectedRole.replace('ROLE_', '') : '';
    const normalizedCurrent = currentRole ? currentRole.replace('ROLE_', '') : '';

    if (this.authService.hasToken() && normalizedCurrent === normalizedExpected) {
      return true;
    }

    alert('Access Denied: Role Mismatch.');
    this.router.navigate(['/']); 
    return false;
  }
}