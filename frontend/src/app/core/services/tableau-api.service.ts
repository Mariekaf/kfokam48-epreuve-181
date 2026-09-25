import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { apiBaseUrl } from '../config/api.config';
import { TableauEtudiant } from '../models/tableau';

/**
 * Endpoint reel : `GET /api/tableau?promotionId=...`
 *
 * La moyenne provient directement du backend : elle n'est jamais
 * recalculee dans le frontend (ENF15 / RG18).
 */
@Injectable({ providedIn: 'root' })
export class TableauApiService {
  private readonly http = inject(HttpClient);
  private readonly base = apiBaseUrl();

  consulterTableau(promotionId: number): Observable<TableauEtudiant[]> {
    const params = new HttpParams().set('promotionId', promotionId);

    return this.http.get<TableauEtudiant[]>(`${this.base}/tableau`, { params });
  }
}
