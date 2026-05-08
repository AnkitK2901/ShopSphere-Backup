import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:9090/auth';
  
  // This helps our Navbar know if the user is logged in instantly
  private loggedInSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isLoggedIn$ = this.loggedInSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

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
    return this.http.post(`${this.apiUrl}/register`, user);
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
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
      // Decode the JWT payload (the middle part of the token)
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.role || '';
    } catch (e) {
      return '';
    }
  }
}