export type StatutRelecture = 'A_FAIRE' | 'EN_COURS' | 'RELU';

export interface SubmitRelectureRequest {
  readonly note: number;
  readonly commentaire: string;
}

export interface RelectureResume {
  readonly id: number;
  readonly exerciceId: number;
  readonly statut: StatutRelecture;
}
