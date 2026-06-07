import axiosInstance from "@/lib/axios/axiosInstance";

/**
 * Admin endpoints (via gateway, base `/api`). Admin operations live on the
 * auth service, so they sit under `/auth`.
 *
 * ─── ENDPOINT ASSUMPTIONS (adjust here if the backend differs) ───────────────
 *   GET  /auth/pharmacies/pending        → list pharmacies awaiting approval
 *   PUT  /auth/pharmacies/{id}/approve    → approve a pharmacy
 *   PUT  /auth/pharmacies/{id}/reject     → reject a pharmacy
 *   GET  /auth/users                      → list users  (UNCONFIRMED — no endpoint
 *                                           existed in the Phase 1 snapshot)
 *
 * If approve/reject is actually the single endpoint
 *   PUT /auth/pharmacies/{id}/approval?approve=true|false
 * swap the two functions below — nothing else changes.
 * ─────────────────────────────────────────────────────────────────────────────
 */

const PHARMACY_BASE = "/auth/pharmacies";
const USERS_BASE = "/auth/users";

/** Normalises array | PagedResponse | { pharmacies|users|items: [...] } into a common shape. */
function normalizeList(data) {
  if (Array.isArray(data)) {
    return { items: data, totalElements: data.length, totalPages: 1, page: 0 };
  }
  const items = data?.content ?? data?.pharmacies ?? data?.users ?? data?.items ?? [];
  return {
    items,
    totalElements: data?.totalElements ?? data?.totalCount ?? items.length,
    totalPages: data?.totalPages ?? 1,
    page: data?.page ?? 0,
  };
}

export async function getPendingPharmacies({ page = 0, size = 20 } = {}) {
  const { data } = await axiosInstance.get(`${PHARMACY_BASE}/pending`, {
    params: { page, size },
  });
  return normalizeList(data);
}

export async function approvePharmacy(pharmacyId) {
  const { data } = await axiosInstance.put(`${PHARMACY_BASE}/${pharmacyId}/approve`);
  return data;
}

export async function rejectPharmacy(pharmacyId) {
  const { data } = await axiosInstance.put(`${PHARMACY_BASE}/${pharmacyId}/reject`);
  return data;
}

export async function getUsers({ page = 0, size = 20 } = {}) {
  const { data } = await axiosInstance.get(USERS_BASE, { params: { page, size } });
  return normalizeList(data);
}

// ─── Defensive field accessors (tolerate different DTO field names) ──────────
export const pharmacyId = (p) => p?.id ?? p?.pharmacyId;
export const pharmacyName = (p) => p?.pharmacyName ?? p?.name ?? "—";
export const userId = (u) => u?.id ?? u?.userId;
export const userName = (u) =>
  u?.displayName ?? ([u?.firstName, u?.lastName].filter(Boolean).join(" ") || "—");
export const userStatus = (u) => u?.status ?? u?.accountStatus ?? "—";
