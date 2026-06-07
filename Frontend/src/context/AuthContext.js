import { createContext } from "react";

/**
 * Holds the authenticated user and auth actions.
 * Shape: {
 *   user: { userId, email, role } | null,
 *   status: "loading" | "authenticated" | "unauthenticated",
 *   isAuthenticated: boolean,
 *   login, logout, registerCustomer, registerPharmacy
 * }
 * Provided by <AuthProvider> (context/Context.jsx); consumed via useAuth().
 */
export const AuthContext = createContext(null);
