import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useMedicines } from "@/features/customer/hooks/useMedicines";
import { useGeolocation } from "@/features/customer/hooks/useGeolocation";
import PharmacyResultsView from "@/features/customer/components/PharmacyResultsView";

const RADIUS_OPTIONS = [1, 2, 5, 10, 25, 50];

export default function MedicineSearchPage() {
  const [query, setQuery] = useState("");
  const [radiusKm, setRadiusKm] = useState(5);
  const [queryError, setQueryError] = useState("");

  const geo = useGeolocation();
  const { results, meta, loading, error, hasSearched, search, loadMore } = useMedicines();

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!query.trim()) {
      setQueryError("Enter a medicine name to search.");
      return;
    }
    setQueryError("");
    search({
      query: query.trim(),
      lat: geo.coords?.lat,
      lng: geo.coords?.lng,
      radiusKm: geo.coords ? radiusKm : undefined,
      size: 20,
    });
  };

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="font-heading text-xl font-semibold">Find a medicine</h1>
        <p className="text-sm text-muted-foreground">
          Search across nearby pharmacies and see who has it in stock.
        </p>
      </div>

      {/* Search controls */}
      <form onSubmit={handleSubmit} className="flex flex-col gap-3">
        <div className="flex flex-col gap-3 sm:flex-row">
          <div className="flex-1">
            <Input
              value={query}
              onChange={(e) => {
                setQuery(e.target.value);
                setQueryError("");
              }}
              placeholder="e.g. Paracetamol"
              aria-invalid={!!queryError}
              className="h-10"
            />
          </div>
          <Button type="submit" size="lg" disabled={loading}>
            {loading ? "Searching…" : "Search"}
          </Button>
        </div>

        <div className="flex flex-wrap items-center gap-3">
          <Button
            type="button"
            variant={geo.coords ? "secondary" : "outline"}
            size="sm"
            onClick={geo.request}
            disabled={geo.loading}
          >
            {geo.loading ? "Locating…" : geo.coords ? "Location set ✓" : "Use my location"}
          </Button>

          <label className="flex items-center gap-2 text-sm text-muted-foreground">
            Within
            <select
              value={radiusKm}
              onChange={(e) => setRadiusKm(Number(e.target.value))}
              disabled={!geo.coords}
              className="h-8 rounded-lg border border-input bg-transparent px-2 text-sm outline-none focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50 disabled:opacity-50"
            >
              {RADIUS_OPTIONS.map((r) => (
                <option key={r} value={r}>
                  {r} km
                </option>
              ))}
            </select>
          </label>

          {!geo.coords && (
            <span className="text-xs text-muted-foreground">
              Share your location to sort results by distance.
            </span>
          )}
        </div>

        {queryError && <p className="text-xs text-destructive">{queryError}</p>}
        {geo.error && <p className="text-xs text-destructive">{geo.error}</p>}
      </form>

      {/* States */}
      {error && (
        <p className="rounded-lg bg-destructive/10 px-3 py-2 text-sm text-destructive">{error}</p>
      )}

      {!hasSearched && !loading && (
        <p className="rounded-xl bg-card p-6 text-center text-sm text-muted-foreground ring-1 ring-foreground/10">
          Search for a medicine to see nearby pharmacies.
        </p>
      )}

      {hasSearched && !loading && results.length === 0 && !error && (
        <p className="rounded-xl bg-card p-6 text-center text-sm text-muted-foreground ring-1 ring-foreground/10">
          No pharmacies found for “{query}”. Try a different name
          {geo.coords ? " or widen your radius." : "."}
        </p>
      )}

      {loading && results.length === 0 && (
        <div className="flex justify-center py-10">
          <div className="size-6 animate-spin rounded-full border-2 border-muted border-t-foreground" />
        </div>
      )}

      {/* Results + map */}
      {results.length > 0 && (
        <PharmacyResultsView
          results={results}
          userCoords={geo.coords}
          header={
            <p className="text-xs text-muted-foreground">
              {meta.totalElements} result{meta.totalElements === 1 ? "" : "s"}
            </p>
          }
          footer={
            !meta.last && (
              <Button
                variant="outline"
                onClick={loadMore}
                disabled={loading}
                className="self-center"
              >
                {loading ? "Loading…" : "Load more"}
              </Button>
            )
          }
        />
      )}
    </div>
  );
}
