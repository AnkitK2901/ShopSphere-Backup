import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { ToastService } from '../services/toast.service'; 

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  constructor(
    private router: Router,
    private toastService: ToastService,
  ) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler,
  ): Observable<HttpEvent<unknown>> {
    const token = localStorage.getItem('jwt_token');

    let handledRequest = request;

    if (token) {
      handledRequest = request.clone({
        headers: request.headers.set('Authorization', `Bearer ${token}`),
      });
    }

    return next.handle(handledRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        // FIX: Removed the 403 check. Only clear local storage on true 401 expiration.
        if (error.status === 401) {
          console.warn('Session expired. Logging out.');

          // Clear all user data
          localStorage.removeItem('jwt_token');
          localStorage.removeItem('userId');

          this.toastService.showError(
            'Your session has expired. Please log in again.',
          );

          this.router.navigate(['/login']);
        }
        return throwError(() => error);
      }),
    );
  }
}