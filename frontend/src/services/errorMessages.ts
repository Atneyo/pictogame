import type { ApiErrorCode } from "../types/api.ts";
import { ApiError } from "./ApiError.ts";

// Record<ApiErrorCode, ...> forces a message for every code: adding a code without one fails to compile
const MESSAGES: Record<ApiErrorCode, string> = {
  INVALID_WORD_COUNT: "Le nombre de mots demandé n'est pas valide.",
  INVALID_PARAMETER: "Un paramètre de la requête n'est pas valide.",
  WORD_NOT_FOUND: "Ce mot n'existe pas.",
  DUPLICATE_WORD: "Ce mot existe déjà.",
  DATABASE_UNAVAILABLE:
    "La base de données est momentanément indisponible. Réessayez dans un instant.",
  INTERNAL_ERROR: "Une erreur inattendue est survenue côté serveur.",
  NETWORK_ERROR: "Impossible de joindre le serveur. Vérifiez qu'il est démarré.",
  SERVER_ERROR: "Erreur interne du serveur. Vérifiez qu'il est démarré.",
  UNKNOWN_ERROR: "Une erreur inconnue est survenue.",
};

/** Returns a user-friendly French message for any error thrown by the API layer. */
export function getErrorMessage(error: unknown): string {
  if (error instanceof ApiError) {
    // The backend may send a code this version of the frontend does not know yet
    return MESSAGES[error.code] ?? MESSAGES.UNKNOWN_ERROR;
  }
  return MESSAGES.UNKNOWN_ERROR;
}
