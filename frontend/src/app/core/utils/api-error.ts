import { HttpErrorResponse } from '@angular/common/http';

import { ApiError } from '../models/api-error';

/**
 * Message technique affiche lorsqu'aucun message lisible n'est disponible.
 * Aucune stack trace ni erreur Java brute n'est exposee a l'utilisateur.
 */
const MESSAGE_GENERIQUE = 'Une erreur inattendue est survenue. Veuillez réessayer.';

const MESSAGE_SERVEUR_INJOIGNABLE =
  "Le serveur n'est pas joignable. Vérifiez que le backend est démarré sur le port 8080.";

/**
 * Normalise une erreur HTTP en `ApiError`.
 *
 * Le backend renvoie toujours `{ code, message }`. Si le corps ne respecte pas
 * ce format (endpoint absent, erreur de passerelle, corps vide), un message
 * generique est produit plutot que d'afficher une erreur technique brute.
 */
export function normaliserErreurApi(error: unknown): ApiError {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return {
        code: 'SERVEUR_INJOIGNABLE',
        message: MESSAGE_SERVEUR_INJOIGNABLE,
        status: 0
      };
    }

    const corps = error.error;

    if (corps !== null && typeof corps === 'object') {
      const candidat = corps as Partial<ApiError>;

      if (typeof candidat.code === 'string' && candidat.code.length > 0) {
        return {
          code: candidat.code,
          message:
            typeof candidat.message === 'string' && candidat.message.length > 0
              ? candidat.message
              : MESSAGE_GENERIQUE,
          status: error.status
        };
      }
    }

    return {
      code: 'ERREUR_TECHNIQUE',
      message: messagePourStatut(error.status),
      status: error.status
    };
  }

  return {
    code: 'ERREUR_TECHNIQUE',
    message: MESSAGE_GENERIQUE,
    status: 0
  };
}

function messagePourStatut(statut: number): string {
  if (statut === 404) {
    return "Ce service n'est pas disponible sur le backend (endpoint absent).";
  }

  if (statut >= 500) {
    return 'Le serveur a rencontré un problème. Veuillez réessayer.';
  }

  return MESSAGE_GENERIQUE;
}

/**
 * Retourne un message affichable a l'utilisateur pour n'importe quelle erreur.
 */
export function messageErreur(error: unknown): string {
  if (error !== null && typeof error === 'object' && 'message' in error) {
    const message = (error as { message?: unknown }).message;

    if (typeof message === 'string' && message.length > 0) {
      return message;
    }
  }

  return MESSAGE_GENERIQUE;
}
