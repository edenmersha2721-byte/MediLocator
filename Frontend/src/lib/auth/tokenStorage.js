import { jwtDecode } from "jwt-decode";

/**
 * Token storage + JWT decoding.
 *
 * The backend login response (AuthResponse) does NOT include the user id —
 * the id, email and role live inside the JWT access token:
 *   { sub: <userId>, email, role: "CUSTOMER" | "PHARMACY" | "ADMIN", exp }
 * so we decode the token to derive the authenticated user.
 */

const ACCESS_TOKEN_KEY = "ml.accessToken";
const REFRESH_TOKEN_KEY = "ml.refreshToken";

export function getAccessToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function getRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY);
}

export function setTokens({ accessToken, refreshToken }) {
  if (accessToken) localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  if (refreshToken) localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
}

export function clearTokens() {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
}

/**
 * Decodes the access token into the authenticated user, or null if the token
 * is missing/invalid.
 * @returns {{ userId: string, email: string, role: string, exp: number } | null}
 */
export function decodeUser(token) {
  if (!token) return null;
  try {
    const claims = jwtDecode(token);
    if (!claims?.sub) return null;
    return {
      userId: claims.sub,
      email: claims.email ?? null,
      role: claims.role ?? null,
      exp: claims.exp ?? null,
    };
  } catch {
    return null;
  }
}

/** True when the token is absent or its `exp` is in the past. */
export function isTokenExpired(token) {
  const user = decodeUser(token);
  if (!user?.exp) return true;
  // exp is in seconds; add a 10s clock-skew buffer.
  return user.exp * 1000 <= Date.now() + 10_000;
}
