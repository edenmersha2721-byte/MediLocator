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

/** Google Maps directions deep-link to a destination (optionally from an origin). */
export function googleMapsDirectionsUrl(destLat, destLng, originLat, originLng) {
  const base = "https://www.google.com/maps/dir/?api=1";
  const dest = `&destination=${destLat},${destLng}`;
  const origin =
    originLat != null && originLng != null ? `&origin=${originLat},${originLng}` : "";
  return `${base}${origin}${dest}`;
}
