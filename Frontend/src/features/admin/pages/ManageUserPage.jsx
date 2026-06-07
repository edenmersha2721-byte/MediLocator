import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { useUsers } from "@/features/admin/hooks/useUsers";
import { userId, userName, userStatus } from "@/features/admin/api/adminApi";
import { cn } from "@/lib/utils";

const ROLE_STYLES = {
  ADMIN: "bg-purple-100 text-purple-700",
  PHARMACY: "bg-blue-100 text-blue-700",
  CUSTOMER: "bg-emerald-100 text-emerald-700",
};

export default function ManageUserPage() {
  const { items, page, setPage, meta, loading, error } = useUsers();
  const totalPages = meta.totalPages || 0;

  return (
    <div className="flex flex-col gap-5">
      <div>
        <h1 className="font-heading text-xl font-semibold">Users</h1>
        <p className="text-sm text-muted-foreground">
          {meta.totalElements} registered user{meta.totalElements === 1 ? "" : "s"}
        </p>
      </div>

      {error && (
        <p className="rounded-lg bg-destructive/10 px-3 py-2 text-sm text-destructive">{error}</p>
      )}

      <div className="rounded-xl bg-card ring-1 ring-foreground/10">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Email</TableHead>
              <TableHead>Role</TableHead>
              <TableHead>Status</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading && (
              <TableRow>
                <TableCell colSpan={4} className="py-10 text-center text-muted-foreground">
                  Loading users…
                </TableCell>
              </TableRow>
            )}

            {!loading && items.length === 0 && !error && (
              <TableRow>
                <TableCell colSpan={4} className="py-10 text-center text-muted-foreground">
                  No users to display.
                </TableCell>
              </TableRow>
            )}

            {!loading &&
              items.map((u) => (
                <TableRow key={userId(u)}>
                  <TableCell className="font-medium text-foreground">{userName(u)}</TableCell>
                  <TableCell className="text-muted-foreground">{u.email ?? "—"}</TableCell>
                  <TableCell>
                    <span
                      className={cn(
                        "rounded-full px-2 py-0.5 text-xs font-medium",
                        ROLE_STYLES[u.role] ?? "bg-muted text-muted-foreground"
                      )}
                    >
                      {u.role ?? "—"}
                    </span>
                  </TableCell>
                  <TableCell className="text-muted-foreground">{userStatus(u)}</TableCell>
                </TableRow>
              ))}
          </TableBody>
        </Table>
      </div>

      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-3">
          <Button
            variant="outline"
            size="sm"
            disabled={page === 0 || loading}
            onClick={() => setPage((p) => Math.max(0, p - 1))}
          >
            Previous
          </Button>
          <span className="text-sm text-muted-foreground">
            Page {page + 1} of {totalPages}
          </span>
          <Button
            variant="outline"
            size="sm"
            disabled={page >= totalPages - 1 || loading}
            onClick={() => setPage((p) => p + 1)}
          >
            Next
          </Button>
        </div>
      )}
    </div>
  );
}
