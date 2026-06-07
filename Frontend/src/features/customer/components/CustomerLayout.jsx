import { NavLink, Outlet } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/features/auth/hooks/useAuth";
import { PATHS } from "@/router/routes";
import { cn } from "@/lib/utils";

const NAV_LINKS = [
  { to: PATHS.CUSTOMER_HOME, label: "Find medicine" },
  { to: PATHS.CUSTOMER_PRESCRIPTIONS, label: "Prescription" },
];

/** Shell for customer pages: top bar with brand + nav + account/logout, then routed content. */
export default function CustomerLayout() {
  const { user, logout } = useAuth();

  return (
    <div className="flex min-h-screen flex-col bg-muted/20">
      <header className="sticky top-0 z-[1100] border-b bg-background/95 backdrop-blur">
        <div className="mx-auto flex w-full max-w-6xl items-center justify-between gap-4 px-4 py-3">
          <div className="flex items-center gap-6">
            <span className="font-heading text-base font-semibold">Medicine Locator</span>
            <nav className="flex items-center gap-1">
              {NAV_LINKS.map((link) => (
                <NavLink
                  key={link.to}
                  to={link.to}
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
