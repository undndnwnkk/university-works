import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  private authService = inject(AuthService);

  credentials = {
    username: '',
    password: ''
  };

  onLogin() {
    if (this.validate()) {
      this.authService.login(this.credentials);
    }
  }

  onRegister() {
    if (this.validate()) {
      this.authService.register(this.credentials);
    }
  }

  private validate(): boolean {
    if (!this.credentials.username || !this.credentials.password) {
      alert('Заполните все поля!');
      return false;
    }
    return true;
  }
}