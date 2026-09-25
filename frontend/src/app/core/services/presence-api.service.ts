import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { apiBaseUrl } from '../config/api.config';
import { MarkPresenceRequest, MarkPresenceResponse } from '../models/presence';

/**
 * Endpoint reel : `POST /api/presences`
 *
 * Le nombre de tentatives de l'etudiant n'est jamais compte ni simule
 * cote frontend : seule la reponse du backend est affichee.
 */
@Injectable({ providedIn: 'root' })
export class PresenceApiService {
  private readonly http = inject(HttpClient);
  private readonly base = apiBaseUrl();

  marquerPresence(request: MarkPresenceRequest): Observable<MarkPresenceResponse> {
    return this.http.post<MarkPresenceResponse>(`${this.base}/presences`, request);
  }
}
