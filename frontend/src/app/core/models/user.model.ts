export interface User {
  id?: number;
  username: string;
  email: string;
  roles: string[];
}

export interface AuthResponse {
  token: string;
  username: string;
  email: string;
  roles: string[];
}
