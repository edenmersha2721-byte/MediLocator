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
import { cn } from "@/lib/utils";
import { useInventory } from "@/features/pharmacy/hooks/useInventory";
import MedicineFormDialog from "@/features/pharmacy/components/MedicineFormDialog";
import DeleteMedicineDialog from "@/features/pharmacy/components/DeleteMedicineDialog";

const TODAY = new Date().toISOString().slice(0, 10);

function formatPrice(price) {
  return price != null ? `ETB ${Number(price).toFixed(2)}` : "—";
}

export default function InventoryPage() {
  const { items, page, setPage, meta, loading, error, create, update, remove } = useInventory();

  // null = closed; { medicine: <m|null> } = add/edit; deleting holds a medicine.
  const [editing, setEditing] = useState(null);
  const [deleting, setDeleting] = useState(null);

  const totalPages = meta.totalPages || 0;

  return (
    <div className="flex flex-col gap-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="font-heading text-xl font-semibold">Inventory</h1>
          <p className="text-sm text-muted-foreground">
            {meta.totalElements} medicine{meta.totalElements === 1 ? "" : "s"} in your catalogue
          </p>
        </div>
        <Button onClick={() => setEditing({ medicine: null })}>Add medicine</Button>
      </div>

      {error && (
        <p className="rounded-lg bg-destructive/10 px-3 py-2 text-sm text-destructive">{error}</p>
      )}

      <div className="rounded-xl bg-card ring-1 ring-foreground/10">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Medicine</TableHead>
              <TableHead>Category</TableHead>
              <TableHead className="text-right">Price</TableHead>
              <TableHead className="text-right">Stock</TableHead>
              <TableHead>Expiry</TableHead>
              <TableHead>Rx</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading && (
              <TableRow>
                <TableCell colSpan={7} className="py-10 text-center text-muted-foreground">
                  Loading inventory…
                </TableCell>
              </TableRow>
            )}

            {!loading && items.length === 0 && (
              <TableRow>
                <TableCell colSpan={7} className="py-10 text-center text-muted-foreground">
                  No medicines yet. Click “Add medicine” to create your first entry.
                </TableCell>
              </TableRow>
            )}

            {!loading &&
              items.map((m) => {
                const expired = m.expiryDate && m.expiryDate < TODAY;
                const outOfStock = !m.stockQuantity;
                return (
                  <TableRow key={m.id}>
                    <TableCell>
                      <span className="font-medium text-foreground">{m.medicineName}</span>
                      {(m.brandName || m.genericName) && (
                        <span className="block text-xs text-muted-foreground">
                          {[m.brandName, m.genericName].filter(Boolean).join(" • ")}
                        </span>
                      )}
                    </TableCell>
                    <TableCell className="text-muted-foreground">{m.category || "—"}</TableCell>
                    <TableCell className="text-right">{formatPrice(m.price)}</TableCell>
                    <TableCell className="text-right">
                      <span className={cn(outOfStock && "font-medium text-destructive")}>
                        {m.stockQuantity}
                      </span>
                    </TableCell>
                    <TableCell>
                      {m.expiryDate ? (
                        <span className={cn(expired && "font-medium text-destructive")}>
                          {m.expiryDate}
                          {expired ? " (expired)" : ""}
                        </span>
                      ) : (
                        "—"
                      )}
                    </TableCell>
                    <TableCell>{m.requiresPrescription ? "Yes" : "No"}</TableCell>
                    <TableCell className="text-right">
                      <div className="flex justify-end gap-1">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => setEditing({ medicine: m })}
                        >
                          Edit
                        </Button>
                        <Button
                          variant="destructive"
                          size="sm"
                          onClick={() => setDeleting(m)}
                        >
                          Delete
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                );
              })}
          </TableBody>
        </Table>
      </div>

      {/* Pagination */}
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

      {/* Add / Edit dialog — keyed per target for a clean form seed */}
      {editing && (
        <MedicineFormDialog
          key={editing.medicine?.id ?? "new"}
          initialValue={editing.medicine}
          onClose={() => setEditing(null)}
          onSubmit={(payload) =>
            editing.medicine ? update(editing.medicine.id, payload) : create(payload)
          }
        />
      )}

      {/* Delete confirmation */}
      {deleting && (
        <DeleteMedicineDialog
          medicine={deleting}
          onClose={() => setDeleting(null)}
          onConfirm={() => remove(deleting.id)}
        />
      )}
    </div>
  );
}
