import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { CartService } from './cart.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:9090/api/auth';
  
  private loggedInSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isLoggedIn$ = this.loggedInSubject.asObservable();

  constructor(
    private http: HttpClient, 
    private router: Router,
    private cartService: CartService 
  ) {}

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      tap((response: any) => {
        if (response && response.token) {
          localStorage.setItem('jwt_token', response.token);
          this.loggedInSubject.next(true);
        }
      })
    );
  }

  register(user: any): Observable<any> {
    // UPDATED: Now expecting a JSON object (RegisterResponse) from the backend
    return this.http.post(`${this.apiUrl}/register`, user);
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
    this.cartService.clearCart(); 
    this.loggedInSubject.next(false);
    this.router.navigate(['/login']);
  }

  hasToken(): boolean {
    return !!localStorage.getItem('jwt_token');
  }

  getUserRole(): string {
    const token = localStorage.getItem('jwt_token');
    if (!token) return '';
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.role || '';
    } catch (e) {
      return '';
    }
  }
}