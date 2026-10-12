import { useCallback } from "react";
import { useSearchParams } from "react-router-dom";

// keeps page / search / filters in the url so they survive refresh and back navigation
export function useQueryState() {
  const [searchParams, setSearchParams] = useSearchParams();

  const get = useCallback((key, fallback = "") => searchParams.get(key) ?? fallback, [searchParams]);

  const set = useCallback(
    (patch) => {
      setSearchParams(
        (current) => {
          const next = new URLSearchParams(current);
          Object.entries(patch).forEach(([key, value]) => {
            if (value === undefined || value === null || value === "") next.delete(key);
            else next.set(key, String(value));
          });
          return next;
        },
        { replace: true }
      );
    },
    [setSearchParams]
  );

  return [get, set];
}

// ?page=&size= from the url as numbers
export function usePaging(defaultSize = 20) {
  const [get, set] = useQueryState();
  const page = Math.max(1, Number.parseInt(get("page", "1"), 10) || 1);
  const size = Math.min(500, Math.max(1, Number.parseInt(get("size", String(defaultSize)), 10) || defaultSize));
  return { page, size, get, set };
}
