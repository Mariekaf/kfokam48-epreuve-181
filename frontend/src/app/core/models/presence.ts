export type SourcePresence = 'ETUDIANT' | 'FORMATEUR';

export interface MarkPresenceRequest {
  readonly code: string;
  readonly etudiantId: number;
}

export interface MarkPresenceResponse {
  readonly id: number;
  readonly sessionId: number;
  readonly etudiantId: number;
  readonly source: SourcePresence;
}
