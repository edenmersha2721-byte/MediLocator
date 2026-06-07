import { NavLink, Outlet } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/features/auth/hooks/useAuth";
import { PATHS } from "@/router/routes";
import { cn } from "@/lib/utils";

const NAV_LINKS = [
  { to: PATHS.ADMIN_HOME, label: "Overview", end: true },
  { to: PATHS.ADMIN_PHARMACIES, label: "Pharmacies" },
  { to: PATHS.ADMIN_USERS, label: "Users" },
];

/** Shell for admin pages: top bar with brand + nav + logout, then routed content. */
export default function AdminLayout() {
  const { user, logout } = useAuth();

  return (
    <div className="flex min-h-screen flex-col bg-muted/20">
      <header className="sticky top-0 z-30 border-b bg-background/95 backdrop-blur">
        <div className="mx-auto flex w-full max-w-6xl items-center justify-between gap-4 px-4 py-3">
          <div className="flex items-center gap-6">
            <div className="flex items-center gap-2">
              <span className="font-heading text-base font-semibold">Medicine Locator</span>
              <span className="rounded-full bg-muted px-2 py-0.5 text-xs font-medium text-muted-foreground">
                Admin
              </span>
            </div>
            <nav className="flex items-center gap-1">
              {NAV_LINKS.map((link) => (
                <NavLink
                  key={link.to}
                  to={link.to}
                  end={link.end}
                  className={({ isActive }) =>
                    cn(
                      "rounded-lg px-3 py-1.5 text-sm font-medium transition-colors",
                      isActive
                        ? "bg-muted text-foreground"
                        : "text-muted-foreground hover:text-foreground"
                    )
                  }
                >
                  {link.label}
                </NavLink>
              ))}
            </nav>
          </div>
          <div className="flex items-center gap-3">
            <span className="hidden text-sm text-muted-foreground sm:inline">{user?.email}</span>
            <Button variant="outline" size="sm" onClick={logout}>
              Log out
            </Button>
          </div>
        </div>
      </header>
      <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-6">
        <Outlet />
      </main>
    </div>
  );
}
