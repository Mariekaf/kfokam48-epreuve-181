import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { apiBaseUrl } from '../config/api.config';
import { RelectureResume, SubmitRelectureRequest } from '../models/relecture';

/**
 * Endpoints :
 *   - `POST /api/relectures/{id}` : operation implementee par le backend ;
 *   - `GET  /api/relectures?relecteurId=` : declare dans `api/contrat.yaml`
 *     mais ABSENT du backend actuel. L'appel est conserve : il ne
 *     fonctionnera des que l'operation sera implementee cote backend.
 */
@Injectable({ providedIn: 'root' })
export class RelectureApiService {
  private readonly http = inject(HttpClient);
  private readonly base = apiBaseUrl();

  rendreRelecture(
    relectureId: number,
    request: SubmitRelectureRequest
  ): Observable<void> {
    return this.http.post<void>(`${this.base}/relectures/${relectureId}`, request);
  }

  listerPourRelecteur(relecteurId: number): Observable<RelectureResume[]> {
    const params = new HttpParams().set('relecteurId', relecteurId);

    return this.http.get<RelectureResume[]>(`${this.base}/relectures`, { params });
  }
}
