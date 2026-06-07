import { buttonVariants } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { formatDistance, googleMapsDirectionsUrl } from "@/lib/helpers/helpers";

/**
 * A single medicine-at-pharmacy result.
 * Clicking the card selects it (the map flies to that pharmacy).
 *
 * @param item       NearbyMedicineResponse row
 * @param userCoords {lat,lng} | null (used for directions origin)
 * @param active     whether this card is the selected one
 * @param onSelect   () => void
 */
export default function MedicineResultCard({ item, userCoords, active, onSelect }) {
  const price = item.price != null ? `ETB ${Number(item.price).toFixed(2)}` : "—";

  return (
    <div
      role="button"
      tabIndex={0}
      onClick={onSelect}
      onKeyDown={(e) => {
        if (e.key === "Enter" || e.key === " ") {
          e.preventDefault();
          onSelect();
        }
      }}
      className={cn(
        "flex cursor-pointer flex-col gap-2 rounded-xl bg-card p-4 text-sm ring-1 transition-colors",
        active ? "ring-2 ring-primary" : "ring-foreground/10 hover:ring-foreground/20"
      )}
    >
      <div className="flex items-start justify-between gap-2">
        <div>
          <p className="font-medium text-foreground">{item.medicineName}</p>
          {(item.brandName || item.genericName) && (
            <p className="text-xs text-muted-foreground">
              {[item.brandName, item.genericName].filter(Boolean).join(" • ")}
            </p>
          )}
        </div>
        <span
          className={cn(
            "shrink-0 rounded-full px-2 py-0.5 text-xs font-medium",
            item.available && item.stockQuantity > 0
              ? "bg-emerald-100 text-emerald-700"
              : "bg-muted text-muted-foreground"
          )}
        >
          {item.available && item.stockQuantity > 0 ? `In stock (${item.stockQuantity})` : "Out of stock"}
        </span>
      </div>

      <div className="flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-muted-foreground">
        {item.category && <span className="rounded bg-muted px-1.5 py-0.5">{item.category}</span>}
        <span className="font-medium text-foreground">{price}</span>
      </div>

      <div className="mt-1 border-t pt-2">
        <p className="font-medium text-foreground">{item.pharmacyName}</p>
        <p className="text-xs text-muted-foreground">
          {item.address}
          {item.city ? `, ${item.city}` : ""}
        </p>
        <div className="mt-2 flex items-center justify-between">
          {item.distanceMeters != null ? (
            <span className="text-xs font-medium text-foreground">
              {formatDistance(item.distanceMeters)} away
            </span>
          ) : (
            <span />
          )}
          <a
            href={googleMapsDirectionsUrl(
              item.latitude,
              item.longitude,
              userCoords?.lat,
              userCoords?.lng
            )}
            target="_blank"
            rel="noreferrer"
            onClick={(e) => e.stopPropagation()}
            className={buttonVariants({ variant: "outline", size: "sm" })}
          >
            Navigate
          </a>
        </div>
      </div>
    </div>
  );
}
