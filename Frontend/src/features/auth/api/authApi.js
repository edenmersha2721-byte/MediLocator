import axiosInstance from "@/lib/axios/axiosInstance";

/**
 * Auth service endpoints (via gateway, base `/api`).
 * Backend routes: POST /auth/login, /auth/register/customer, /auth/register/pharmacy,
 * /auth/logout, GET /auth/me.
 */

export async function login({ email, password }) {
  const { data } = await axiosInstance.post("/auth/login", { email, password });
  return data; // AuthResponse: { accessToken, refreshToken, tokenType, accessTokenExpiresIn, role }
}

export async function registerCustomer(payload) {
  const { data } = await axiosInstance.post("/auth/register/customer", payload);
  return data; // MessageResponse
}

export async function registerPharmacy(payload) {
  const { data } = await axiosInstance.post("/auth/register/pharmacy", payload);
  return data; // MessageResponse
}

export async function getCurrentUser() {
  const { data } = await axiosInstance.get("/auth/me");
  return data; // UserProfileResponse: { id, email, role, displayName, emailVerified }
}

export async function refreshSession(refreshToken) {
  // `/auth/refresh` is in the interceptor's bypass list, so this won't recurse.
  const { data } = await axiosInstance.post("/auth/refresh", null, {
    params: { refreshToken },
  });
  return data; // AuthResponse
}

export async function logout(refreshToken) {
  const { data } = await axiosInstance.post("/auth/logout", null, {
    params: refreshToken ? { refreshToken } : undefined,
  });
  return data;
}
