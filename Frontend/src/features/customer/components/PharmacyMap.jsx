import { useEffect, useMemo, useRef } from "react";
import { MapContainer, TileLayer, Marker, Popup, CircleMarker, useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";
import { buttonVariants } from "@/components/ui/button";
import { formatDistance, googleMapsDirectionsUrl } from "@/lib/helpers/helpers";

// Fix Leaflet's default marker icon paths under a bundler (Vite).
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

const ADDIS_ABABA = { lat: 8.9806, lng: 38.7578 }; // sensible default center

/** Fits the map to all points whenever the set of points changes. */
function FitToPoints({ points }) {
  const map = useMap();
  useEffect(() => {
    if (points.length === 0) return;
    if (points.length === 1) {
      map.setView([points[0].lat, points[0].lng], 15);
      return;
    }
    const bounds = L.latLngBounds(points.map((p) => [p.lat, p.lng]));
    map.fitBounds(bounds, { padding: [48, 48], maxZoom: 16 });
  }, [points, map]);
  return null;
}

/** Flies to and opens the popup for the selected pharmacy. */
function FlyToSelected({ selected, markerRefs }) {
  const map = useMap();
  useEffect(() => {
    if (!selected) return;
    map.flyTo([selected.latitude, selected.longitude], 16, { duration: 0.6 });
    const marker = markerRefs.current[selected.pharmacyId];
    if (marker) marker.openPopup();
  }, [selected, map, markerRefs]);
  return null;
}

/**
 * OpenStreetMap (Leaflet) view of the user and nearby pharmacies.
 *
 * @param userCoords  {lat,lng} | null
 * @param pharmacies  deduped pharmacy rows (NearbyMedicineResponse-shaped)
 * @param selected    the currently selected pharmacy row | null
 */
export default function PharmacyMap({ userCoords, pharmacies, selected }) {
  const markerRefs = useRef({});

  const fitPoints = useMemo(() => {
    const pts = pharmacies.map((p) => ({ lat: p.latitude, lng: p.longitude }));
    if (userCoords) pts.push(userCoords);
    return pts;
  }, [pharmacies, userCoords]);

  const center = userCoords ?? (pharmacies[0]
    ? { lat: pharmacies[0].latitude, lng: pharmacies[0].longitude }
    : ADDIS_ABABA);

  return (
    <MapContainer
      center={[center.lat, center.lng]}
      zoom={13}
      scrollWheelZoom
      className="h-full w-full rounded-xl"
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />

      {userCoords && (
        <CircleMarker
          center={[userCoords.lat, userCoords.lng]}
          radius={8}
          pathOptions={{ color: "#2563eb", fillColor: "#3b82f6", fillOpacity: 0.9 }}
        >
          <Popup>You are here</Popup>
        </CircleMarker>
      )}

      {pharmacies.map((p) => (
        <Marker
          key={p.pharmacyId}
          position={[p.latitude, p.longitude]}
          ref={(ref) => {
            if (ref) markerRefs.current[p.pharmacyId] = ref;
          }}
        >
          <Popup>
            <div className="space-y-1">
              <p className="font-medium">{p.pharmacyName}</p>
              <p className="text-xs text-muted-foreground">
                {p.address}
                {p.city ? `, ${p.city}` : ""}
              </p>
              {p.distanceMeters != null && (
                <p className="text-xs">{formatDistance(p.distanceMeters)} away</p>
              )}
              <a
                href={googleMapsDirectionsUrl(
                  p.latitude,
                  p.longitude,
                  userCoords?.lat,
                  userCoords?.lng
                )}
                target="_blank"
                rel="noreferrer"
                className={buttonVariants({ variant: "outline", size: "xs", className: "mt-1" })}
              >
                Navigate
              </a>
            </div>
          </Popup>
        </Marker>
      ))}

      <FitToPoints points={fitPoints} />
      <FlyToSelected selected={selected} markerRefs={markerRefs} />
    </MapContainer>
  );
}
