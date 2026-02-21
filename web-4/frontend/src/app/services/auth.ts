import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs'; 

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  login(credentials: any) {
    return this.http.post<{ token: string }>('/api/auth/login', credentials).pipe(
      tap({
        next: (res) => {
          localStorage.setItem('jwt_token', res.token);
          this.router.navigate(['/main']);
        },
      }),
    );
  }

  register(credentials: any) {

    return this.http.post<{ token: string }>('/api/auth/register', credentials).pipe(
      tap({
        next: (res) => {
          localStorage.setItem('jwt_token', res.token);
          this.router.navigate(['/main']);
        },
      }),
    );
  }

  logout() {
    localStorage.removeItem('jwt_token');
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('jwt_token');
  }

  getToken() {
    return localStorage.getItem('jwt_token');
  }
}
