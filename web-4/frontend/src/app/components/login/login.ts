import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth';
import { NgIf } from '@angular/common';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [FormsModule, NgIf],
    templateUrl: './login.html',
    styleUrl: './login.css'
})
export class LoginComponent {
    private authService = inject(AuthService);

    errorMessage = "";

    credentials = {
        username: '',
        password: ''
    };

    onLogin() {
        this.errorMessage = '';

        if (this.validate()) {
            this.authService.login(this.credentials).subscribe({
                error: (err) => {
                    this.errorMessage = `Ошибка входа номер: ${err?.status}, ответ: ${err.error?.error}`;
                }
            });
        }
    }

    onRegister() {
        this.errorMessage = '';
        this.authService.register(this.credentials).subscribe({
            error: (err) => 
                this.errorMessage = `Ошибка входа номер: ${err?.status}, ответ: ${err.error?.error}`
        })
    }

    private validate(): boolean {
        if (!this.credentials.username || !this.credentials.password) {
            this.errorMessage = "Заполните все поля!";
            return false;
        }
        return true;
    }
}