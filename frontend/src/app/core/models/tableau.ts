export interface TableauEtudiant {
  readonly etudiantId: number;
  readonly nom: string;
  readonly presences: number;
  readonly exercicesDeposes: number;
  readonly moyenne: number | null;
  readonly relecturesEnAttente: number;
}
