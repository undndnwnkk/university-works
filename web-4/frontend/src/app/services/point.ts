import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Point } from '../models/types';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PointService {
  private http = inject(HttpClient);

  addPoint(point: {x: number, y: number, r: number}): Observable<Point> {
    return this.http.post<Point>('/api/points', point);
  }

  getPoints(): Observable<Point[]> {
    return this.http.get<Point[]>('/api/points');
  }
}