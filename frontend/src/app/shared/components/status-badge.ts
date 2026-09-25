import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

export type TonBadge = 'neutral' | 'info' | 'success' | 'warning' | 'danger';

/**
 * Badge de statut metier.
 *
 * La traduction des statuts techniques du backend (`A_FAIRE`, `EN_COURS`,
 * `RELU`, `OUVERTE`...) en libelles lisibles est centralisee ici afin
 * d'etre coherente dans toute l'application.
 */
@Component({
  selector: 'app-status-badge',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<span class="badge" [class]="'badge badge-' + ton()">{{ libelle() }}</span>`
})
export class StatusBadge {
  readonly statut = input.required<string>();

  readonly libelle = computed(() => LIBELLES[this.statut()] ?? this.statut());

  readonly ton = computed<TonBadge>(() => TONS[this.statut()] ?? 'neutral');
}

const LIBELLES: Readonly<Record<string, string>> = {
  A_FAIRE: 'À faire',
  EN_COURS: 'En cours',
  RELU: 'Rendu',
  OUVERTE: 'Ouverte',
  TERMINEE: 'Terminée',
  CLOTUREE: 'Clôturée',
  DEPOSE: 'Déposé',
  EN_ATTENTE_RELECTURE: 'En attente de relecture',
  ETUDIANT: 'Étudiant',
  FORMATEUR: 'Formateur'
};

const TONS: Readonly<Record<string, TonBadge>> = {
  A_FAIRE: 'warning',
  EN_COURS: 'info',
  RELU: 'success',
  OUVERTE: 'success',
  TERMINEE: 'warning',
  CLOTUREE: 'neutral',
  DEPOSE: 'info',
  EN_ATTENTE_RELECTURE: 'warning',
  ETUDIANT: 'info',
  FORMATEUR: 'neutral'
};

/** Libelle lisible d'un statut technique. */
export function libelleStatut(statut: string): string {
  return LIBELLES[statut] ?? statut;
}
