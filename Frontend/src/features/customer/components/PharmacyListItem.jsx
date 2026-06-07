import { PlusIcon, MapPinIcon } from "lucide-react";
import { cn } from "@/lib/utils";
import { formatDistance } from "@/lib/helpers/helpers";
import { medicineStockStatus } from "@/features/customer/stock";

/**
 * Horizontal pharmacy result row (search list style).
 * Clicking selects it (the map flies to that pharmacy).
 */
export default function PharmacyListItem({ item, active, nearest, onSelect }) {
  const status = medicineStockStatus(item);
  const price = item.price != null ? `ETB ${Number(item.price).toFixed(2)}` : "—";

  return (
    <button
      onClick={onSelect}
      className={cn(
        "flex w-full items-center gap-4 rounded-2xl border bg-card p-4 text-left transition-all duration-200",
        active
          ? "border-indigo-500/60 ring-1 ring-indigo-500/40"
          : "border-foreground/5 hover:border-foreground/15 hover:shadow-md hover:shadow-foreground/5"
      )}
    >
      {/* Logo medallion (tinted by stock) */}
      <span
        className={cn("flex size-12 shrink-0 items-center justify-center rounded-2xl", status.badge)}
      >
        <PlusIcon className="size-6" />
      </span>

      {/* Name + address + distance */}
      <div className="min-w-0 flex-1">
        <div className="flex items-center gap-2">
          <p className="truncate font-semibold text-foreground">{item.pharmacyName}</p>
          {nearest && (
            <span className="rounded-full bg-indigo-500/10 px-2 py-0.5 text-[11px] font-medium text-indigo-600">
              Nearest
            </span>
          )}
        </div>
        <p className="truncate text-xs text-muted-foreground">
          {item.address}
          {item.city ? `, ${item.city}` : ""}
        </p>
        {item.distanceMeters != null && (
          <p className="mt-0.5 flex items-center gap-1 text-xs font-medium text-foreground">
            <MapPinIcon className="size-3 text-indigo-600" />
            {formatDistance(item.distanceMeters)} away
          </p>
        )}
      </div>

      {/* Stock */}
      <span
        className={cn(
          "hidden shrink-0 items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-medium sm:inline-flex",
          status.badge
        )}
      >
        <span className={cn("size-1.5 rounded-full", status.dot)} />
        {status.label}
      </span>

      {/* Price + CTA */}
      <div className="shrink-0 text-right">
        <p className="font-semibold tracking-tight text-foreground">{price}</p>
        <span className="text-xs font-medium text-indigo-600">View on map →</span>
      </div>
    </button>
  );
}
