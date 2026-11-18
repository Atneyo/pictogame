// Error codes sent by the backend (see ErrorCode.java), plus client-side codes
export type ApiErrorCode =
  | "INVALID_WORD_COUNT"
  | "INVALID_LABEL"
  | "INVALID_LABEL_NULL"
  | "INVALID_LABEL_DUPLICATE"
  | "INVALID_PARAMETER"
  | "DUPLICATE_WORD"
  | "DATABASE_UNAVAILABLE"
  | "INTERNAL_ERROR"
  | "NETWORK_ERROR" // client-side: the server could not be reached
  | "SERVER_ERROR" // Error 5xx
  | "UNKNOWN_ERROR"; // client-side: unexpected response

// Shape of the JSON body returned by the backend on errors (RFC 9457 ProblemDetail + our fields)
export interface ProblemDetail {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
  code: ApiErrorCode;
  timestamp: string;
}
