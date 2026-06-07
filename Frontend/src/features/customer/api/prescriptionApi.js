import axiosInstance from "@/lib/axios/axiosInstance";

/** Content types the backend accepts for a prescription upload. */
export const ACCEPTED_FILE_TYPES = ["image/jpeg", "image/jpg", "image/png", "application/pdf"];
export const ACCEPT_ATTR = ".jpg,.jpeg,.png,.pdf,image/jpeg,image/png,application/pdf";

/**
 * Upload a prescription image/PDF.
 *
 * POST /prescriptions/upload  (multipart/form-data)
 *   fields: file (required), latitude, longitude, radiusKm (optional)
 * The backend stores the file, runs OCR, extracts medicine names, and forwards
 * them to the search service — returning both in one response:
 *
 *   ExtractedMedicinesResponse {
 *     prescriptionId,
 *     extractedMedicines: string[],
 *     pharmacyResults: PagedResponse<NearbyMedicineResponse>,
 *     message
 *   }
 *
 * @param onUploadProgress optional axios progress callback
 */
export async function uploadPrescription(
  { file, lat, lng, radiusKm },
  { onUploadProgress } = {}
) {
  const formData = new FormData();
  formData.append("file", file);
  if (lat != null && lng != null) {
    formData.append("latitude", lat);
    formData.append("longitude", lng);
    if (radiusKm != null) formData.append("radiusKm", radiusKm);
  }

  const { data } = await axiosInstance.post("/prescriptions/upload", formData, {
    // Let axios/browser set the multipart boundary for FormData.
    headers: { "Content-Type": "multipart/form-data" },
    onUploadProgress,
  });
  return data;
}
