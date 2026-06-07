import axiosInstance from "@/lib/axios/axiosInstance";

/**
 * Search service endpoints (via gateway, base `/api`).
 *
 * Medicine search:  GET /search?query&lat&lng&radiusKm&category&page&size
 *   → PagedResponse<NearbyMedicineResponse>
 * Each result row carries both the medicine and its owning pharmacy
 * (name, address, city, latitude, longitude, distanceMeters) so the same
 * payload drives both the results list and the map markers.
 *
 * Note: the backend forces requiresPrescription=false on this public search,
 * so Rx-only items are intentionally not discoverable here.
 */
export async function searchMedicines({
  query,
  lat,
  lng,
  radiusKm,
  category,
  page = 0,
  size = 20,
}) {
  const params = { query, page, size };
  // radiusKm is only meaningful with coordinates (backend validation).
  if (lat != null && lng != null) {
    params.lat = lat;
    params.lng = lng;
    if (radiusKm != null) params.radiusKm = radiusKm;
  }
  if (category) params.category = category;

  const { data } = await axiosInstance.get("/search", { params });
  return data;
}
