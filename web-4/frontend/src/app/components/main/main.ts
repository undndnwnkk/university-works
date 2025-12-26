import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PointService } from '../../services/point';
import { AuthService } from '../../services/auth';
import { Point } from '../../models/types';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './main.html',
  styleUrl: './main.css'
})
export class Main implements OnInit {
  private pointService = inject(PointService);
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  x: number = 0;
  y: number = 0;
  r: number = 3;

  points: Point[] = [];
  scale: number = 30;

  get numR(): number {
    return Number(this.r);
  }

  ngOnInit() {
    this.loadPoints();
  }

  loadPoints() {
    this.pointService.getPoints().subscribe({
      next: (data) => {
        this.points = [...data];
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  isHitClient(x: number, y: number, r: number): boolean {
    if (r === 0) return false;
    if (r > 0) {
      if (x >= 0 && y >= 0) return (x * x + y * y) <= (r / 2) * (r / 2);
      if (x <= 0 && y <= 0) return y >= -x - (r / 2);
      if (x >= 0 && y <= 0) return x <= r && y >= -r;
    } else {
      const ar = Math.abs(r);
      if (x <= 0 && y <= 0) return (x * x + y * y) <= (ar / 2) * (ar / 2);
      if (x >= 0 && y >= 0) return y <= -x + (ar / 2);
      if (x <= 0 && y >= 0) return x >= -ar && y <= ar;
    }
    return false;
  }

  onSend() {
    this.sendPoint(Number(this.x), Number(this.y), this.numR);
  }

  sendPoint(x: number, y: number, r: number) {
    this.pointService.addPoint({ x, y, r }).subscribe({
      next: () => {
        this.loadPoints();
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  onSvgClick(event: MouseEvent) {
    if (this.numR === 0) return;
    const svg = event.currentTarget as SVGSVGElement;
    const rect = svg.getBoundingClientRect();
    const mouseX = event.clientX - rect.left;
    const mouseY = event.clientY - rect.top;
    const mathX = (mouseX - 150) / this.scale;
    const mathY = (150 - mouseY) / this.scale;
    this.sendPoint(Number(mathX.toFixed(3)), Number(mathY.toFixed(3)), this.numR);
  }

  abs(val: number): number {
    return Math.abs(val);
  }

  logout() {
    this.authService.logout();
  }
}