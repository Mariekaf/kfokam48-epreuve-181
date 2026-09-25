import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { apiBaseUrl } from '../config/api.config';
import { CreateExerciceRequest, CreateExerciceResponse } from '../models/exercice';

/**
 * Endpoint reel : `POST /api/exercices`
 */
@Injectable({ providedIn: 'root' })
export class ExerciceApiService {
  private readonly http = inject(HttpClient);
  private readonly base = apiBaseUrl();

  deposerExercice(request: CreateExerciceRequest): Observable<CreateExerciceResponse> {
    return this.http.post<CreateExerciceResponse>(`${this.base}/exercices`, request);
  }
}
