/**
 * Format d'erreur impose par le backend pour toutes les erreurs.
 * Une reponse 4xx/5xx contient toujours `{ code, message }`.
 */
export interface ApiError {
  readonly code: string;
  readonly message: string;
  readonly status: number;
}
