import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "@/features/auth/hooks/useAuth";
import { roleHome } from "@/lib/auth/roles";

/**
 * Route guard.
 * - While auth state is resolving, renders a lightweight loader.
 * - Unauthenticated users are sent to /login (original location preserved).
 * - When `allowedRoles` is set, users without a permitted role are redirected
 *   to their own role's home.
 *
 * Usage: <Route element={<ProtectedRoute allowedRoles={["ADMIN"]} />}> ... </Route>
 */
export default function ProtectedRoute({ allowedRoles }) {
  const { status, isAuthenticated, user } = useAuth();
  const location = useLocation();

  if (status === "loading") {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div
          className="size-6 animate-spin rounded-full border-2 border-muted border-t-foreground"
          aria-label="Loading"
        />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  if (allowedRoles && !allowedRoles.includes(user?.role)) {
    return <Navigate to={roleHome(user?.role)} replace />;
  }

  return <Outlet />;
}
