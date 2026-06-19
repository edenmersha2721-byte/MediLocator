/**
 * Extracts a human-readable message from an Axios error, tolerant of the
 * different error shapes returned by the gateway and the individual services
 * (some return { message }, some { error }, validation errors return { errors }).
 */
export function extractApiError(error, fallback = "Something went wrong. Please try again.") {
  const data = error?.response?.data;

  if (!error?.response) {
    // No response = network/CORS/gateway-down.
    return "Cannot reach the server. Check your connection and try again.";
  }
  if (typeof data === "string" && data.trim()) return data;
  if (data?.message) return data.message;
  if (data?.error && typeof data.error === "string") return data.error;

  // Bean-validation style: { errors: { field: msg } } or [{ field, message }]
  if (data?.errors) {
    if (Array.isArray(data.errors)) {
      const first = data.errors[0];
      if (first?.message) return first.message;
      if (typeof first === "string") return first;
    } else if (typeof data.errors === "object") {
      const first = Object.values(data.errors)[0];
      if (first) return String(first);
    }
  }
  return fallback;
}

/** Formats a distance in metres as "450 m" or "1.2 km". */
export function formatDistance(meters) {
  if (meters == null || Number.isNaN(meters)) return "";
  if (meters < 1000) return `${Math.round(meters)} m`;
  return `${(meters / 1000).toFixed(1)} km`;
}

/** Formats an ISO date/datetime as "Jun 20, 2026", or "—" if absent/invalid. */
export function formatDate(iso) {
  if (!iso) return "—";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return "—";
  return d.toLocaleDateString(undefined, { year: "numeric", month: "short", day: "numeric" });
}

/** Relative time like "2h ago" from an ISO date, or "" if absent/invalid. */
export function timeAgo(iso) {
  if (!iso) return "";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return "";
  const s = Math.floor((Date.now() - d.getTime()) / 1000);
  if (s < 60) return "just now";
  const m = Math.floor(s / 60);
  if (m < 60) return `${m}m ago`;
  const h = Math.floor(m / 60);
  if (h < 24) return `${h}h ago`;
  const days = Math.floor(h / 24);
  if (days < 30) return `${days}d ago`;
  const mo = Math.floor(days / 30);
  if (mo < 12) return `${mo}mo ago`;
  return `${Math.floor(mo / 12)}y ago`;
}

/** Google Maps directions deep-link to a destination (optionally from an origin). */
export function googleMapsDirectionsUrl(destLat, destLng, originLat, originLng) {
  const base = "https://www.google.com/maps/dir/?api=1";
  const dest = `&destination=${destLat},${destLng}`;
  const origin =
    originLat != null && originLng != null ? `&origin=${originLat},${originLng}` : "";
  return `${base}${origin}${dest}`;
}
