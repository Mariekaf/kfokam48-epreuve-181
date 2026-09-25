import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface Promotion { id:number; nom:string; }
export interface Etudiant { id:number; nom:string; }
export interface Session { id:number; titre:string; code:string; statut:string; ouvertureAt:string; expirationAt:string; finAt?:string|null; clotureAt?:string|null; promotionId:number; }
export interface Tableau { etudiantId:number; nom:string; presences:number; exercicesDeposes:number; moyenne:number|null; relecturesEnAttente:number; }
export interface Relecture { id:number; exerciceId:number; statut:string; lien?:string; note?:number|null; commentaire?:string|null; }
export interface ExerciceResultat { exerciceId:number; sessionId:number; lien:string; statut:string; note:number|null; commentaire:string|null; }
export interface ApiError { code:string; message:string; }

@Injectable({providedIn:'root'})
export class ApiService {
  private readonly base = '/api';
  constructor(private http:HttpClient) {}
  private safe<T>(o:Observable<T>):Observable<T>{ return o.pipe(catchError((e:HttpErrorResponse)=>throwError(()=>e.error?.message ? e.error : {code:'ERREUR_RESEAU',message:'Impossible de joindre le serveur.'} as ApiError))); }
  promotions(){ return this.safe(this.http.get<Promotion[]>(`${this.base}/promotions`)); }
  etudiants(promotionId:number){ return this.safe(this.http.get<Etudiant[]>(`${this.base}/etudiants`,{params:{promotionId}})); }
  sessions(promotionId:number){ return this.safe(this.http.get<Session[]>(`${this.base}/sessions`,{params:{promotionId}})); }
  ouvrirSession(titre:string,promotionId:number){ return this.safe(this.http.post<{id:number;code:string;ouvertureAt:string;expirationAt:string}>(`${this.base}/sessions`,{titre,promotionId})); }
  terminerSession(id:number){ return this.safe(this.http.post<Session>(`${this.base}/sessions/${id}/fin`,{})); }
  cloturerSession(id:number){ return this.safe(this.http.post<Session>(`${this.base}/sessions/${id}/cloture`,{})); }
  presence(code:string,etudiantId:number){ return this.safe(this.http.post(`${this.base}/presences`,{code,etudiantId})); }
  presenceManuelle(sessionId:number,etudiantId:number){ return this.safe(this.http.post(`${this.base}/sessions/${sessionId}/presences`,{etudiantId})); }
  deposerExercice(sessionId:number,etudiantId:number,lien:string){ return this.safe(this.http.post<{id:number;statut:string}>(`${this.base}/exercices`,{sessionId,etudiantId,lien})); }
  modifierExercice(id:number,lien:string){ return this.safe(this.http.patch(`${this.base}/exercices/${id}`,{lien})); }
  resultats(etudiantId:number){ return this.safe(this.http.get<ExerciceResultat[]>(`${this.base}/etudiants/${etudiantId}/exercices`)); }
  relectures(relecteurId:number){ return this.safe(this.http.get<Relecture[]>(`${this.base}/relectures`,{params:{relecteurId}})); }
  detailRelecture(id:number,relecteurId:number){ return this.safe(this.http.get<Relecture>(`${this.base}/relectures/${id}/detail`,{params:{relecteurId}})); }
  commencerRelecture(id:number,relecteurId:number){ return this.safe(this.http.post<Relecture>(`${this.base}/relectures/${id}/commencer`,{relecteurId})); }
  rendreRelecture(id:number,note:number,commentaire:string){ return this.safe(this.http.post<void>(`${this.base}/relectures/${id}`,{note,commentaire})); }
  corrigerRelecture(id:number,relecteurId:number,note:number,commentaire:string){ return this.safe(this.http.patch<void>(`${this.base}/relectures/${id}/correction`,{relecteurId,note,commentaire})); }
  tableau(promotionId:number){ return this.safe(this.http.get<Tableau[]>(`${this.base}/tableau`,{params:{promotionId}})); }
}
