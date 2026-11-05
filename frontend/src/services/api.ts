import type { Word } from "../types/word.ts";
import { ApiError, toApiError } from "./ApiError.ts";

// Relative path on purpose: in dev the Vite proxy forwards /api to the backend
const API_BASE = "/api";

/** Shared fetch wrapper: handles network failures, error responses and JSON parsing. */
async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  try {
    response = await fetch(`${API_BASE}${path}`, init);
  } catch (error) {
    // A cancelled request is not a failure: let the caller deal with it
    if (error instanceof DOMException && error.name === "AbortError") {
      throw error;
    }
    throw new ApiError(0, "NETWORK_ERROR", "Unable to reach the server");
  }

  if (!response.ok) {
    throw await toApiError(response);
  }

  // works only if all endpoints return JSON, eg. 204 No Content: error
  return (await response.json()) as T;
}

/**
 * Fetches `count` random words (the backend uses its default when omitted).
 * Pass an AbortSignal to cancel the request, e.g. when a component unmounts.
 */
export function getWords(count?: number, signal?: AbortSignal): Promise<Word[]> {
  const params = new URLSearchParams();
  if (count !== undefined) {
    params.set("count", String(count));
  }
  const query = params.size > 0 ? `?${params}` : "";
  return request<Word[]>(`/words${query}`, { signal });
}
