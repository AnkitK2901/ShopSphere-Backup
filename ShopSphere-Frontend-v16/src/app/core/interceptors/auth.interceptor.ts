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
import { ToastService } from '../services/toast.service'; // THE FIX: Imported ToastService

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  // THE FIX: Injected ToastService into the constructor
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

    // Catch 401s globally so the app doesn't hang on dead tokens
    return next.handle(handledRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 || error.status === 403) {
          console.warn('Session expired or unauthorized. Logging out.');

          // Clear all user data
          localStorage.removeItem('jwt_token');
          localStorage.removeItem('userId');

          // THE FIX: Trigger the visible UI Toast Notification
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
