import { Injectable, computed, signal } from '@angular/core';

export interface IdentiteEtudiant {
  readonly promotionId: number;
  readonly promotionNom: string | null;
  readonly etudiantId: number;
  readonly etudiantNom: string | null;
}

const CLE_SESSION = 'kfokam48.identite';

function lireSession(): IdentiteEtudiant | null {
  try {
    const brut = sessionStorage.getItem(CLE_SESSION);

    if (brut === null) {
      return null;
    }

    const parse = JSON.parse(brut) as Partial<IdentiteEtudiant>;

    if (typeof parse.promotionId !== 'number' || typeof parse.etudiantId !== 'number') {
      return null;
    }

    return {
      promotionId: parse.promotionId,
      promotionNom: parse.promotionNom ?? null,
      etudiantId: parse.etudiantId,
      etudiantNom: parse.etudiantNom ?? null
    };
  } catch {
    return null;
  }
}

/**
 * Etat partage entre l'espace etudiant et l'espace relecteur.
 *
 * Conformement a RG1, aucun mot de passe n'est demande : l'etudiant
 * selectionne simplement son identite. Elle est conservee le temps de la
 * session du navigateur (`sessionStorage`).
 */
@Injectable({ providedIn: 'root' })
export class EtatIdentiteService {
  private readonly identite = signal<IdentiteEtudiant | null>(lireSession());

  /** Derniere session pour laquelle l'etudiant a marque sa presence. */
  private readonly sessionCourante = signal<number | null>(
    this.identite() === null
      ? null
      : Number(sessionStorage.getItem('kfokam48.sessionId') ?? '') || null
  );

  readonly identiteCourante = this.identite.asReadonly();
  readonly aUneIdentite = computed(() => this.identite() !== null);
  readonly sessionIdCourante = this.sessionCourante.asReadonly();

  definirIdentite(identite: IdentiteEtudiant): void {
    this.identite.set(identite);

    try {
      sessionStorage.setItem(CLE_SESSION, JSON.stringify(identite));
    } catch {
      // Le stockage de session peut etre indisponible : l'etitat reste
      // utilisable en memoire pour la duree de l'application.
    }
  }

  memoriserSession(sessionId: number): void {
    this.sessionCourante.set(sessionId);

    try {
      sessionStorage.setItem('kfokam48.sessionId', String(sessionId));
    } catch {
      // Idem : stockage non indispensable.
    }
  }

  effacer(): void {
    this.identite.set(null);
    this.sessionCourante.set(null);

    try {
      sessionStorage.removeItem(CLE_SESSION);
      sessionStorage.removeItem('kfokam48.sessionId');
    } catch {
      // Idem : stockage non indispensable.
    }
  }
}
