// Client for the read-only CRM API (see src/main/java/controller/crm/crm.md)

export const API_BASE = (import.meta.env.VITE_CRM_API || "http://localhost:8081/crm").replace(/\/+$/, "");

export class ApiError extends Error {
  constructor(message, status) {
    super(message);
    this.status = status;
  }
}

export async function crmGet(path, params = {}, signal) {
  const query = new URLSearchParams(
    Object.entries(params).filter(([, value]) => value !== undefined && value !== null && value !== "")
  ).toString();

  let response;
  try {
    response = await fetch(`${API_BASE}${path}${query ? `?${query}` : ""}`, { signal });
  } catch (error) {
    if (error.name === "AbortError") throw error;
    // status 0 = the server could not be reached at all
    throw new ApiError(`Can't reach the CRM API at ${API_BASE}. Is the Java app running?`, 0);
  }

  let body = null;
  try {
    body = await response.json();
  } catch {
    // non-json body, handled below
  }

  if (!response.ok) {
    throw new ApiError(body?.error || `Request failed with status ${response.status}`, response.status);
  }
  return body;
}
