import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService, private toastr: ToastrService, private router: Router) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(catchError((err: HttpErrorResponse) => {
      let errorMsg = '';
      if (err.status === 401) {
        this.authService.logout();
        this.toastr.error('Session expired. Please log in again.', 'Unauthorized');
        this.router.navigate(['/auth/login']);
      } else if (err.status === 403) {
        this.toastr.error('You do not have permission to perform this action.', 'Forbidden');
      } else if (err.status === 404) {
        this.toastr.error('The requested resource was not found.', 'Not Found');
      } else {
        errorMsg = err.error?.message || err.error?.error || err.statusText || 'An unexpected error occurred';
        this.toastr.error(errorMsg, `Error ${err.status !== 0 ? err.status : 'Network'}`);
      }
      return throwError(() => err);
    }));
  }
}
