import axiosInstance from "@/lib/axios/axiosInstance";

/**
 * Inventory service endpoints (via gateway, base `/api`).
 * All routes are scoped to a pharmacy; the backend enforces that the caller
 * owns that pharmacy (currentUser.userId === pharmacyId, role PHARMACY), so
 * `pharmacyId` is always the logged-in user's id.
 */

export async function getInventory(pharmacyId, { availableOnly = false, page = 0, size = 20 } = {}) {
  const { data } = await axiosInstance.get(`/inventory/pharmacies/${pharmacyId}/medicines`, {
    params: { availableOnly, page, size },
  });
  return data; // PharmacyInventoryResponse
}

export async function addMedicine(pharmacyId, payload) {
  const { data } = await axiosInstance.post(
    `/inventory/pharmacies/${pharmacyId}/medicines`,
    payload
  );
  return data; // MedicineResponse
}

export async function updateMedicine(pharmacyId, medicineId, payload) {
  const { data } = await axiosInstance.put(
    `/inventory/pharmacies/${pharmacyId}/medicines/${medicineId}`,
    payload
  );
  return data; // MedicineResponse
}

export async function deleteMedicine(pharmacyId, medicineId) {
  const { data } = await axiosInstance.delete(
    `/inventory/pharmacies/${pharmacyId}/medicines/${medicineId}`
  );
  return data; // MessageResponse
}
