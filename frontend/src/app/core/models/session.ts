export type StatutSession = 'OUVERTE' | 'TERMINEE' | 'CLOTUREE';

export interface CreateSessionRequest {
  readonly titre: string;
  readonly promotionId: number;
}

export interface CreateSessionResponse {
  readonly id: number;
  readonly code: string;
  readonly ouvertureAt: string;
  readonly expirationAt: string;
}
