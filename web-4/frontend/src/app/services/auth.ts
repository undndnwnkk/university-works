import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  login(credentials: any) {
    this.http.post<{ token: string }>('/api/auth/login', credentials).subscribe({
      next: (res) => {
        localStorage.setItem('jwt_token', res.token);
        this.router.navigate(['/main']);
      },
      error: (err) => alert('Ошибка входа: ' + (err.error?.error || 'Неверный логин/пароль')),
    });
  }

  register(credentials: any) {
    this.http.post<{ token: string }>('/api/auth/register', credentials).subscribe({
      next: (res) => {
        localStorage.setItem('jwt_token', res.token);
        this.router.navigate(['/main']);
      },
      error: (err) => alert('Ошибка регистрации: ' + (err.error?.error || 'Логин занят')),
    });
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
