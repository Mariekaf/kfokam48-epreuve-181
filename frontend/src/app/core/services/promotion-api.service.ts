import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { apiBaseUrl } from '../config/api.config';
import { Promotion } from '../models/promotion';

/**
 * Endpoint declare dans `api/contrat.yaml` : `GET /api/promotions`
 * mais ABSENT du backend actuel (aucun `PromotionController`).
 *
 * L'appel est conserve tel quel : l'ecran affiche un message explicite
 * et propose une saisie manuelle de l'identifiant plutot que d'inventer
 * des donnees.
 */
@Injectable({ providedIn: 'root' })
export class PromotionApiService {
  private readonly http = inject(HttpClient);
  private readonly base = apiBaseUrl();

  lister(): Observable<Promotion[]> {
    return this.http.get<Promotion[]>(`${this.base}/promotions`);
  }
}
