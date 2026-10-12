import { useCallback, useEffect, useState } from "react";
import { crmGet } from "../api";

// fetches a CRM route and refetches whenever the path or params change
export function useApi(path, params = {}, { enabled = true } = {}) {
  const [state, setState] = useState({ data: null, error: null, loading: enabled });
  const [reloadKey, setReloadKey] = useState(0);
  const paramsKey = JSON.stringify(params);

  useEffect(() => {
    if (!enabled || !path) {
      setState({ data: null, error: null, loading: false });
      return undefined;
    }

    const controller = new AbortController();
    setState((previous) => ({ ...previous, loading: true, error: null }));

    crmGet(path, JSON.parse(paramsKey), controller.signal)
      .then((data) => setState({ data, error: null, loading: false }))
      .catch((error) => {
        if (error.name !== "AbortError") {
          setState({ data: null, error, loading: false });
        }
      });

    return () => controller.abort();
  }, [path, paramsKey, enabled, reloadKey]);

  const reload = useCallback(() => setReloadKey((key) => key + 1), []);

  return { ...state, reload };
}
