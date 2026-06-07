import { useCallback, useState } from "react";

/**
 * Captures the user's GPS position via the browser Geolocation API.
 * Returns { coords: {lat,lng} | null, loading, error, request(), clear() }.
 */
export function useGeolocation() {
  const [coords, setCoords] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  /**
   * Requests the browser location. Resolves to {lat,lng} on success, or null
   * if unsupported / denied / failed — never rejects — so callers can do
   * best-effort capture: `const coords = await request();`
   */
  const request = useCallback(() => {
    return new Promise((resolve) => {
      if (!navigator.geolocation) {
        setError("Geolocation is not supported by this browser.");
        resolve(null);
        return;
      }
      setLoading(true);
      setError("");
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          const next = { lat: pos.coords.latitude, lng: pos.coords.longitude };
          setCoords(next);
          setLoading(false);
          resolve(next);
        },
        (err) => {
          setError(
            err.code === err.PERMISSION_DENIED
              ? "Location permission denied — showing results without distance sorting."
              : "Could not determine your location. Showing results without distance sorting."
          );
          setLoading(false);
          resolve(null);
        },
        { enableHighAccuracy: true, timeout: 10_000, maximumAge: 60_000 }
      );
    });
  }, []);

  const clear = useCallback(() => {
    setCoords(null);
    setError("");
  }, []);

  return { coords, loading, error, request, clear };
}

export default useGeolocation;
