import { HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

import { normaliserErreurApi } from '../utils/api-error';

/**
 * Convertit toute erreur HTTP en `ApiError` ({ code, message }).
 *
 * Les composants ne recoivent donc jamais une `HttpErrorResponse` et
 * n'affichent jamais de stack trace ni d'erreur Java brute.
 */
export const apiErrorInterceptor: HttpInterceptorFn = (request, next) =>
  next(request).pipe(
    catchError((error: unknown) => throwError(() => normaliserErreurApi(error)))
  );
