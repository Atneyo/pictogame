import type { ApiErrorCode, ProblemDetail } from "../types/api.ts";

/** Error thrown for every failed API call, carrying the HTTP status and the backend error code. */
export class ApiError extends Error {
  readonly status: number;
  readonly code: ApiErrorCode;

  constructor(status: number, code: ApiErrorCode, message: string) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.code = code;
  }
}

/** Converts a non-2xx response into an ApiError. */
export async function toApiError(response: Response): Promise<ApiError> {
  try {
    const problem = (await response.json()) as Partial<ProblemDetail>;
    return new ApiError(
      response.status,
      problem.code ?? "UNKNOWN_ERROR",
      problem.detail ?? response.statusText,
    );
  } catch {
    // The body is not JSON. A 5xx here usually means the proxy could not reach the backend.
    const code = response.status >= 500 ? "SERVER_ERROR" : "UNKNOWN_ERROR";
    return new ApiError(response.status, code, response.statusText);
  }
}
