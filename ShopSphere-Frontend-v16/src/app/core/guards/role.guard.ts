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

    if (this.authService.hasToken() && currentRole === expectedRole) {
      return true;
    }

    alert('Access Denied: You do not have permission to view this page.');
    this.router.navigate(['/']); // Kick them back to the storefront
    return false;
  }
}