export type StatutExercice =
  | 'DEPOSE'
  | 'EN_ATTENTE_RELECTURE'
  | 'EN_COURS'
  | 'RELU';

export interface CreateExerciceRequest {
  readonly sessionId: number;
  readonly etudiantId: number;
  readonly lien: string;
}

export interface CreateExerciceResponse {
  readonly id: number;
  readonly statut: StatutExercice;
}
