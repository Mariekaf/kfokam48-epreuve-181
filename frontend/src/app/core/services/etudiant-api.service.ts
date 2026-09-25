import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { apiBaseUrl } from '../config/api.config';
import { Etudiant } from '../models/etudiant';

/**
 * Endpoint declare dans `api/contrat.yaml` : `GET /api/etudiants?promotionId=...`
 * mais ABSENT du backend actuel (aucun `EtudiantController`).
 *
 * Meme demarche que pour les promotions : appel conserve, message
 * explicite et saisie manuelle de l'identifiant en repli.
 */
@Injectable({ providedIn: 'root' })
export class EtudiantApiService {
  private readonly http = inject(HttpClient);
  private readonly base = apiBaseUrl();

  listerParPromotion(promotionId: number): Observable<Etudiant[]> {
    const params = new HttpParams().set('promotionId', promotionId);

    return this.http.get<Etudiant[]>(`${this.base}/etudiants`, { params });
  }
}
