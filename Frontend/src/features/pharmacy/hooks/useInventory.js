import { useCallback, useEffect, useState } from "react";
import * as inventoryApi from "@/features/pharmacy/api/inventoryApi";
import { useAuth } from "@/features/auth/hooks/useAuth";
import { extractApiError } from "@/lib/helpers/helpers";

const DEFAULT_PAGE_SIZE = 20;

/**
 * Pharmacy inventory state + CRUD logic.
 * pharmacyId is the logged-in user's id. The list is fetched in an effect keyed
 * by page + a reload counter; mutations bump that counter to refetch. CRUD
 * actions reject on failure so callers (the form dialog) can surface the error.
 *
 * @param pageSize results per page (dashboard uses a larger page for stats)
 */
export function useInventory({ pageSize = DEFAULT_PAGE_SIZE } = {}) {
  const { user } = useAuth();
  const pharmacyId = user?.userId;

  const [items, setItems] = useState([]);
  const [page, setPage] = useState(0);
  const [meta, setMeta] = useState({ totalElements: 0, totalPages: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let active = true;
    async function fetchInventory() {
      if (!pharmacyId) return;
      setLoading(true);
      setError("");
      try {
        const data = await inventoryApi.getInventory(pharmacyId, { page, size: pageSize });
        if (!active) return;
        setItems(data.medicines ?? []);
        setMeta({ totalElements: data.totalElements, totalPages: data.totalPages });
      } catch (e) {
        if (active) {
          setError(extractApiError(e, "Could not load inventory."));
          setItems([]);
        }
      } finally {
        if (active) setLoading(false);
      }
    }
    fetchInventory();
    return () => {
      active = false;
    };
  }, [pharmacyId, page, reloadKey, pageSize]);

  const reload = useCallback(() => setReloadKey((k) => k + 1), []);

  const create = useCallback(
    async (payload) => {
      await inventoryApi.addMedicine(pharmacyId, payload);
      reload();
    },
    [pharmacyId, reload]
  );

  const update = useCallback(
    async (medicineId, payload) => {
      await inventoryApi.updateMedicine(pharmacyId, medicineId, payload);
      reload();
    },
    [pharmacyId, reload]
  );

  const remove = useCallback(
    async (medicineId) => {
      await inventoryApi.deleteMedicine(pharmacyId, medicineId);
      // If we just removed the last row of a non-first page, step back (the
      // page change refetches); otherwise reload the current page.
      if (items.length === 1 && page > 0) setPage((p) => p - 1);
      else reload();
    },
    [pharmacyId, reload, items.length, page]
  );

  return { items, page, setPage, meta, loading, error, create, update, remove, reload };
}

export default useInventory;
