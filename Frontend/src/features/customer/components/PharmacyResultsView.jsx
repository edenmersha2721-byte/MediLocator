import { useMemo, useState } from "react";
import MedicineResultCard from "@/features/customer/components/MedicineResultCard";
import PharmacyMap from "@/features/customer/components/PharmacyMap";

/**
 * Shared results UI: a list of medicine-at-pharmacy cards alongside a Leaflet
 * map. Selecting a card flies the map to that pharmacy. Used by both the
 * medicine search page and the prescription upload flow.
 *
 * @param results    NearbyMedicineResponse[] (medicine-per-pharmacy rows)
 * @param userCoords {lat,lng} | null
 * @param header     optional node rendered above the list (e.g. result count)
 * @param footer     optional node rendered below the list (e.g. "Load more")
 */
export default function PharmacyResultsView({ results, userCoords, header, footer }) {
  const [selectedId, setSelectedId] = useState(null);

  // One marker per pharmacy (results are medicine-per-pharmacy rows).
  const pharmacies = useMemo(() => {
    const byId = new Map();
    for (const r of results) {
      if (!byId.has(r.pharmacyId)) byId.set(r.pharmacyId, r);
    }
    return [...byId.values()];
  }, [results]);

  const selected = useMemo(
    () => pharmacies.find((p) => p.pharmacyId === selectedId) ?? null,
    [pharmacies, selectedId]
  );

  return (
    <div className="grid gap-6 lg:grid-cols-2">
      <div className="flex flex-col gap-3">
        {header}
        {results.map((item) => (
          <MedicineResultCard
            key={`${item.medicineId}-${item.pharmacyId}`}
            item={item}
            userCoords={userCoords}
            active={item.pharmacyId === selectedId}
            onSelect={() => setSelectedId(item.pharmacyId)}
          />
        ))}
        {footer}
      </div>

      <div className="h-[60vh] overflow-hidden rounded-xl ring-1 ring-foreground/10 lg:sticky lg:top-20 lg:h-[calc(100vh-7rem)]">
        <PharmacyMap userCoords={userCoords} pharmacies={pharmacies} selected={selected} />
      </div>
    </div>
  );
}
