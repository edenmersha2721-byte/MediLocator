import { useCallback, useEffect, useState } from "react";
import * as adminApi from "@/features/admin/api/adminApi";
import { extractApiError } from "@/lib/helpers/helpers";

const PAGE_SIZE = 20;

/**
 * Pending-pharmacy approvals state.
 * Fetches in an effect keyed by page + reload counter; approve/reject reject on
 * failure so the page can surface a per-row error.
 */
export function usePharmacies() {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(0);
  const [meta, setMeta] = useState({ totalElements: 0, totalPages: 0 });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let active = true;
    async function fetchPending() {
      setLoading(true);
      setError("");
      try {
        const data = await adminApi.getPendingPharmacies({ page, size: PAGE_SIZE });
        if (!active) return;
        setItems(data.items);
        setMeta({ totalElements: data.totalElements, totalPages: data.totalPages });
      } catch (e) {
        if (active) {
          setError(extractApiError(e, "Could not load pending pharmacies."));
          setItems([]);
        }
      } finally {
        if (active) setLoading(false);
      }
    }
    fetchPending();
    return () => {
      active = false;
    };
  }, [page, reloadKey]);

  const reload = useCallback(() => setReloadKey((k) => k + 1), []);

  const approve = useCallback(
    async (id) => {
      await adminApi.approvePharmacy(id);
      reload();
    },
    [reload]
  );

  const reject = useCallback(
    async (id) => {
      await adminApi.rejectPharmacy(id);
      reload();
    },
    [reload]
  );

  return { items, page, setPage, meta, loading, error, approve, reject, reload };
}

export default usePharmacies;
