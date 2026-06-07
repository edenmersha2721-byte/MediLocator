import { useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { usePharmacies } from "@/features/admin/hooks/usePharmacies";
import { pharmacyId, pharmacyName } from "@/features/admin/api/adminApi";
import { extractApiError } from "@/lib/helpers/helpers";

export default function ManagePharmacyPage() {
  const { items, page, setPage, meta, loading, error, approve, reject } = usePharmacies();

  // Track the row being acted on so we can disable just that row + show errors.
  const [processingId, setProcessingId] = useState(null);
  const [actionError, setActionError] = useState("");

  const totalPages = meta.totalPages || 0;

  const run = async (id, fn) => {
    setProcessingId(id);
    setActionError("");
    try {
      await fn(id);
    } catch (e) {
      setActionError(extractApiError(e, "Action failed. Please try again."));
    } finally {
      setProcessingId(null);
    }
  };

  return (
    <div className="flex flex-col gap-5">
      <div>
        <h1 className="font-heading text-xl font-semibold">Pharmacy approvals</h1>
        <p className="text-sm text-muted-foreground">
          {meta.totalElements} pharmac{meta.totalElements === 1 ? "y" : "ies"} awaiting review
        </p>
      </div>

      {error && (
        <p className="rounded-lg bg-destructive/10 px-3 py-2 text-sm text-destructive">{error}</p>
      )}
      {actionError && (
        <p className="rounded-lg bg-destructive/10 px-3 py-2 text-sm text-destructive">
          {actionError}
        </p>
      )}

      <div className="rounded-xl bg-card ring-1 ring-foreground/10">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Pharmacy</TableHead>
              <TableHead>Email</TableHead>
              <TableHead>License</TableHead>
              <TableHead>City</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading && (
              <TableRow>
                <TableCell colSpan={5} className="py-10 text-center text-muted-foreground">
                  Loading pending pharmacies…
                </TableCell>
              </TableRow>
            )}

            {!loading && items.length === 0 && !error && (
              <TableRow>
                <TableCell colSpan={5} className="py-10 text-center text-muted-foreground">
                  No pharmacies awaiting approval.
                </TableCell>
              </TableRow>
            )}

            {!loading &&
              items.map((p) => {
                const id = pharmacyId(p);
                const busy = processingId === id;
                return (
                  <TableRow key={id}>
                    <TableCell className="font-medium text-foreground">{pharmacyName(p)}</TableCell>
                    <TableCell className="text-muted-foreground">{p.email ?? "—"}</TableCell>
                    <TableCell className="text-muted-foreground">{p.licenseNumber ?? "—"}</TableCell>
                    <TableCell className="text-muted-foreground">{p.city ?? "—"}</TableCell>
                    <TableCell className="text-right">
                      <div className="flex justify-end gap-1">
                        <Button size="sm" disabled={busy} onClick={() => run(id, approve)}>
                          {busy ? "…" : "Approve"}
                        </Button>
                        <Button
                          size="sm"
                          variant="destructive"
                          disabled={busy}
                          onClick={() => run(id, reject)}
                        >
                          Reject
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                );
              })}
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
