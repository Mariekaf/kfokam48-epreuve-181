import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { apiBaseUrl } from '../config/api.config';
import { CreateSessionRequest, CreateSessionResponse } from '../models/session';

/**
 * Endpoint reel : `POST /api/sessions`
 *
 * Le backend n'expose aucune operation de lecture des sessions
 * (pas de `GET /api/sessions` dans `api/contrat.yaml`) : le frontend
 * n'en invente donc aucune.
 */
@Injectable({ providedIn: 'root' })
export class SessionApiService {
  private readonly http = inject(HttpClient);
  private readonly base = apiBaseUrl();

  ouvrirSession(request: CreateSessionRequest): Observable<CreateSessionResponse> {
    return this.http.post<CreateSessionResponse>(`${this.base}/sessions`, request);
  }
}
