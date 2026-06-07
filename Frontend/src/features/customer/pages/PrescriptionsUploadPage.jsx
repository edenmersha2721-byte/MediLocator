import { useEffect, useMemo, useRef, useState } from "react";
import { Button } from "@/components/ui/button";
import { useGeolocation } from "@/features/customer/hooks/useGeolocation";
import { usePrescriptionUpload } from "@/features/customer/hooks/usePrescriptionUpload";
import PharmacyResultsView from "@/features/customer/components/PharmacyResultsView";
import { ACCEPTED_FILE_TYPES, ACCEPT_ATTR } from "@/features/customer/api/prescriptionApi";

const RADIUS_OPTIONS = [1, 2, 5, 10, 25, 50];
const MAX_FILE_BYTES = 10 * 1024 * 1024; // 10 MB

export default function PrescriptionsUploadPage() {
  const [file, setFile] = useState(null);
  const [fileError, setFileError] = useState("");
  const [radiusKm, setRadiusKm] = useState(5);
  const inputRef = useRef(null);

  const geo = useGeolocation();
  const { status, progress, error, extractedMedicines, results, message, upload, reset } =
    usePrescriptionUpload();

  const isUploading = status === "uploading";
  const isDone = status === "done";

  // Image preview URL, derived from the selected file and revoked on change.
  const previewUrl = useMemo(
    () => (file && file.type !== "application/pdf" ? URL.createObjectURL(file) : ""),
    [file]
  );
  useEffect(() => {
    return () => {
      if (previewUrl) URL.revokeObjectURL(previewUrl);
    };
  }, [previewUrl]);

  const selectFile = (picked) => {
    if (!picked) return;
    if (!ACCEPTED_FILE_TYPES.includes(picked.type)) {
      setFileError("Unsupported file type. Upload a JPG, PNG, or PDF.");
      return;
    }
    if (picked.size > MAX_FILE_BYTES) {
      setFileError("File is too large (max 10 MB).");
      return;
    }
    setFileError("");
    setFile(picked);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    selectFile(e.dataTransfer.files?.[0]);
  };

  const handleUpload = async () => {
    if (!file) {
      setFileError("Choose a prescription file first.");
      return;
    }
    // Best-effort location: use coords we already have, otherwise prompt now.
    // The browser permission dialog is the "ask"; if denied we proceed without.
    const coords = geo.coords ?? (await geo.request());
    upload({
      file,
      lat: coords?.lat,
      lng: coords?.lng,
      radiusKm: coords ? radiusKm : undefined,
    });
  };

  const startOver = () => {
    reset();
    setFile(null);
    setFileError("");
  };

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="font-heading text-xl font-semibold">Upload a prescription</h1>
        <p className="text-sm text-muted-foreground">
          We&apos;ll read the medicines from your prescription and find nearby pharmacies that
          stock them.
        </p>
      </div>

      {!isDone && (
        <div className="flex flex-col gap-4">
          {/* Dropzone */}
          <div
            onDrop={handleDrop}
            onDragOver={(e) => e.preventDefault()}
            onClick={() => inputRef.current?.click()}
            role="button"
            tabIndex={0}
            onKeyDown={(e) => {
              if (e.key === "Enter" || e.key === " ") {
                e.preventDefault();
                inputRef.current?.click();
              }
            }}
            className="flex cursor-pointer flex-col items-center justify-center gap-2 rounded-xl border-2 border-dashed border-input bg-card p-8 text-center transition-colors hover:border-ring"
          >
            <input
              ref={inputRef}
              type="file"
              accept={ACCEPT_ATTR}
              className="hidden"
              onChange={(e) => selectFile(e.target.files?.[0])}
            />
            {file ? (
              <div className="flex flex-col items-center gap-2">
                {previewUrl ? (
                  <img
                    src={previewUrl}
                    alt="Prescription preview"
                    className="max-h-48 rounded-lg object-contain ring-1 ring-foreground/10"
                  />
                ) : (
                  <div className="rounded-lg bg-muted px-4 py-3 text-sm font-medium">PDF document</div>
                )}
                <p className="text-sm font-medium text-foreground">{file.name}</p>
                <p className="text-xs text-muted-foreground">Click to choose a different file</p>
              </div>
            ) : (
              <>
                <p className="text-sm font-medium text-foreground">
                  Drag &amp; drop, or click to choose a file
                </p>
                <p className="text-xs text-muted-foreground">JPG, PNG or PDF · up to 10 MB</p>
              </>
            )}
          </div>
          {fileError && <p className="text-xs text-destructive">{fileError}</p>}

          {/* Location (optional) */}
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
                Share your location to sort pharmacies by distance.
              </span>
            )}
          </div>
          {geo.error && <p className="text-xs text-destructive">{geo.error}</p>}

          {/* Upload action */}
          <div className="flex items-center gap-3">
            <Button size="lg" onClick={handleUpload} disabled={!file || isUploading || geo.loading}>
              {geo.loading ? "Getting location…" : isUploading ? "Processing…" : "Upload & find medicines"}
            </Button>
            {isUploading && (
              <div className="flex-1">
                <div className="h-2 w-full overflow-hidden rounded-full bg-muted">
                  <div
                    className="h-full bg-primary transition-all"
                    style={{ width: `${progress || 5}%` }}
                  />
                </div>
                <p className="mt-1 text-xs text-muted-foreground">
                  {progress < 100 ? `Uploading ${progress}%` : "Reading prescription…"}
                </p>
              </div>
            )}
          </div>

          {status === "error" && (
            <p className="rounded-lg bg-destructive/10 px-3 py-2 text-sm text-destructive">
              {error}
            </p>
          )}
        </div>
      )}

      {/* Results */}
      {isDone && (
        <div className="flex flex-col gap-5">
          <div className="flex flex-wrap items-center justify-between gap-3">
            <h2 className="font-heading text-lg font-semibold">Extracted medicines</h2>
            <Button variant="outline" size="sm" onClick={startOver}>
              Upload another
            </Button>
          </div>

          {extractedMedicines.length > 0 ? (
            <div className="flex flex-wrap gap-2">
              {extractedMedicines.map((name, i) => (
                <span
                  key={`${name}-${i}`}
                  className="rounded-full bg-primary/10 px-3 py-1 text-sm font-medium text-foreground"
                >
                  {name}
                </span>
              ))}
            </div>
          ) : (
            <p className="rounded-xl bg-card p-4 text-sm text-muted-foreground ring-1 ring-foreground/10">
              {message || "No medicines could be read from this prescription."}
            </p>
          )}

          {results.length > 0 ? (
            <PharmacyResultsView
              results={results}
              userCoords={geo.coords}
              header={
                <p className="text-xs text-muted-foreground">
                  Pharmacies stocking your prescribed medicines
                </p>
              }
            />
          ) : (
            extractedMedicines.length > 0 && (
              <p className="rounded-xl bg-card p-6 text-center text-sm text-muted-foreground ring-1 ring-foreground/10">
                No nearby pharmacies found with these medicines in stock
                {geo.coords ? " within your radius." : "."}
              </p>
            )
          )}
        </div>
      )}
    </div>
  );
}
