import { Link } from "react-router-dom";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { usePharmacies } from "@/features/admin/hooks/usePharmacies";
import { PATHS } from "@/router/routes";

export default function AdminDashboardPage() {
  // Reuse the pending-pharmacy hook just to surface the count to monitor.
  const { meta, loading, error } = usePharmacies();

  const cards = [
    {
      to: PATHS.ADMIN_PHARMACIES,
      title: "Pharmacy approvals",
      description: "Review, approve or reject pharmacy registrations.",
      stat: error ? "—" : loading ? "…" : meta.totalElements,
      statLabel: "awaiting review",
    },
    {
      to: PATHS.ADMIN_USERS,
      title: "Users",
      description: "Browse customers, pharmacies and admins on the platform.",
      stat: "View",
      statLabel: "all users",
    },
  ];

  return (
    <div className="flex flex-col gap-5">
      <div>
        <h1 className="font-heading text-xl font-semibold">Overview</h1>
        <p className="text-sm text-muted-foreground">Monitor and manage the platform.</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        {cards.map((c) => (
          <Link key={c.to} to={c.to} className="group">
            <Card className="transition-colors group-hover:ring-foreground/20">
              <CardHeader>
                <CardTitle>{c.title}</CardTitle>
                <CardDescription>{c.description}</CardDescription>
              </CardHeader>
              <CardContent>
                <p className="text-2xl font-semibold text-foreground">{c.stat}</p>
                <p className="text-xs text-muted-foreground">{c.statLabel}</p>
              </CardContent>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  );
}
