export interface User {
  username: string;
  password?: string;
}

export interface Point {
  id?: string;
  x: number;
  y: number;
  r: number;
  hit: boolean;
  executionTime: number;
}