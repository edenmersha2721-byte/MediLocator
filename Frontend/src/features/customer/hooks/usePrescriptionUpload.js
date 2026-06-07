import { useCallback, useState } from "react";
import * as prescriptionApi from "@/features/customer/api/prescriptionApi";
import { extractApiError } from "@/lib/helpers/helpers";

/**
 * Prescription upload state + API logic.
 *
 * status: "idle" | "uploading" | "done" | "error"
 * On success exposes `extractedMedicines` and `results` (the pharmacy rows
 * extracted from the response's pharmacyResults page).
 */
export function usePrescriptionUpload() {
  const [status, setStatus] = useState("idle");
  const [progress, setProgress] = useState(0);
  const [error, setError] = useState("");
  const [data, setData] = useState(null);

  const upload = useCallback(async ({ file, lat, lng, radiusKm }) => {
    setStatus("uploading");
    setError("");
    setProgress(0);
    setData(null);
    try {
      const response = await prescriptionApi.uploadPrescription(
        { file, lat, lng, radiusKm },
        {
          onUploadProgress: (e) => {
            if (e.total) setProgress(Math.round((e.loaded / e.total) * 100));
          },
        }
      );
      setData(response);
      setStatus("done");
    } catch (e) {
      setError(extractApiError(e, "Could not process the prescription. Please try again."));
      setStatus("error");
    }
  }, []);

  const reset = useCallback(() => {
    setStatus("idle");
    setProgress(0);
    setError("");
    setData(null);
  }, []);

  const extractedMedicines = data?.extractedMedicines ?? [];
  // pharmacyResults is a PagedResponse<NearbyMedicineResponse>.
  const results = data?.pharmacyResults?.content ?? [];

  return {
    status,
    progress,
    error,
    data,
    extractedMedicines,
    results,
    message: data?.message ?? "",
    upload,
    reset,
  };
}

export default usePrescriptionUpload;
