import { Outlet } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/features/auth/hooks/useAuth";

/** Shell for pharmacy pages: top bar with brand + account/logout, then routed content. */
export default function PharmacyLayout() {
  const { user, logout } = useAuth();

  return (
    <div className="flex min-h-screen flex-col bg-muted/20">
      <header className="sticky top-0 z-30 border-b bg-background/95 backdrop-blur">
        <div className="mx-auto flex w-full max-w-6xl items-center justify-between gap-4 px-4 py-3">
          <div className="flex items-center gap-3">
            <span className="font-heading text-base font-semibold">Medicine Locator</span>
            <span className="rounded-full bg-muted px-2 py-0.5 text-xs font-medium text-muted-foreground">
              Pharmacy
            </span>
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
