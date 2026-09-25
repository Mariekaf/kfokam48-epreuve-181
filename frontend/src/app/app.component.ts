import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, ApiError, Promotion, Etudiant, Session, Tableau, Relecture, ExerciceResultat } from './api.service';

@Component({
  selector:'app-root', standalone:true, imports:[CommonModule,FormsModule],
  templateUrl:'./app.component.html', styleUrl:'./app.component.css'
})
export class AppComponent implements OnInit {
  role:'formateur'|'etudiant'='formateur'; loading=false; message=''; error='';
  promotions:Promotion[]=[]; etudiants:Etudiant[]=[]; sessions:Session[]=[]; tableau:Tableau[]=[]; relectures:Relecture[]=[]; resultats:ExerciceResultat[]=[];
  promotionId:number|null=null; etudiantId:number|null=null; sessionId:number|null=null;
  titre='Session de cours'; code=''; lien='https://github.com/'; exerciceId:number|null=null;
  note:number|null=null; commentaire=''; selectedRelecture:Relecture|null=null;

  constructor(private api:ApiService){}
  ngOnInit(){ this.loadPromotions(); }
  run<T>(obs:any, success:string, next?:(v:T)=>void){ this.loading=true; this.error=''; this.message=''; obs.subscribe({next:(v:T)=>{this.loading=false;this.message=success;next?.(v);},error:(e:ApiError)=>{this.loading=false;this.error=e?.message||'Une erreur est survenue.';}}); }
  loadPromotions(){ this.run<Promotion[]>(this.api.promotions(),'',(v)=>{this.promotions=v;if(v.length){this.promotionId=v[0].id;this.onPromotion();}}); }
  onPromotion(){ if(!this.promotionId)return; this.run<Etudiant[]>(this.api.etudiants(this.promotionId),'',(v)=>{this.etudiants=v;if(v.length&&!v.some(e=>e.id===this.etudiantId))this.etudiantId=v[0].id;if(this.role==='etudiant')this.loadEtudiant();}); this.api.sessions(this.promotionId).subscribe({next:v=>this.sessions=v,error:()=>this.sessions=[]}); this.loadTableau(); }
  loadSessions(){ if(this.promotionId)this.api.sessions(this.promotionId).subscribe({next:v=>this.sessions=v}); }
  loadTableau(){ if(this.promotionId)this.api.tableau(this.promotionId).subscribe({next:v=>this.tableau=v,error:e=>this.error=e.message}); }
  ouvrir(){ if(!this.promotionId||!this.titre.trim())return; this.run<{id:number;code:string}>(this.api.ouvrirSession(this.titre,this.promotionId),'Session ouverte.',v=>{this.sessionId=v.id;this.message='Session ouverte. Code : '+v.code;this.loadSessions();}); }
  terminer(){ if(!this.sessionId)return; this.run(this.api.terminerSession(this.sessionId),'Session terminée.',()=>this.loadSessions()); }
  cloturer(){ if(!this.sessionId)return; this.run(this.api.cloturerSession(this.sessionId),'Session clôturée.',()=>this.loadSessions()); }
  presenceManuelle(){ if(!this.sessionId||!this.etudiantId)return; this.run(this.api.presenceManuelle(this.sessionId,this.etudiantId),'Présence ajoutée par le formateur.',()=>this.loadTableau()); }
  marquerPresence(){ if(!this.etudiantId||!this.code.trim())return; this.run(this.api.presence(this.code.trim().toUpperCase(),this.etudiantId),'Présence enregistrée.'); }
  deposer(){ if(!this.sessionId||!this.etudiantId||!this.lien.trim())return; this.run<{id:number;statut:string}>(this.api.deposerExercice(this.sessionId,this.etudiantId,this.lien.trim()),'Exercice déposé.',v=>{this.exerciceId=v.id;this.loadEtudiant();}); }
  selectionnerExercice(r:ExerciceResultat){ this.exerciceId=r.exerciceId;this.sessionId=r.sessionId;this.lien=r.lien;this.message='Exercice sélectionné : vous pouvez modifier son lien.';this.error=''; }
  remplacer(){ if(!this.exerciceId||!this.lien.trim())return; this.run(this.api.modifierExercice(this.exerciceId,this.lien.trim()),'Lien remplacé.',()=>this.loadEtudiant()); }
  loadEtudiant(){ if(!this.etudiantId)return; this.api.resultats(this.etudiantId).subscribe({next:v=>this.resultats=v,error:e=>this.error=e.message}); this.api.relectures(this.etudiantId).subscribe({next:v=>this.relectures=v,error:e=>this.error=e.message}); }
  choisirRelecture(r:Relecture){ if(!this.etudiantId)return; this.run<Relecture>(this.api.detailRelecture(r.id,this.etudiantId),'',v=>{this.selectedRelecture=v;this.note=v.note??null;this.commentaire=v.commentaire??'';}); }
  commencer(){ if(!this.selectedRelecture||!this.etudiantId)return; this.run<Relecture>(this.api.commencerRelecture(this.selectedRelecture.id,this.etudiantId),'Relecture commencée.',v=>{this.selectedRelecture={...this.selectedRelecture!,statut:v.statut};this.loadEtudiant();}); }
  rendre(){ if(!this.selectedRelecture||this.note===null||!this.commentaire.trim())return; this.run(this.api.rendreRelecture(this.selectedRelecture.id,this.note,this.commentaire.trim()),'Relecture rendue.',()=>this.loadEtudiant()); }
  corriger(){ if(!this.selectedRelecture||!this.etudiantId||this.note===null||!this.commentaire.trim())return; this.run(this.api.corrigerRelecture(this.selectedRelecture.id,this.etudiantId,this.note,this.commentaire.trim()),'Relecture corrigée.',()=>this.loadEtudiant()); }
  setRole(r:'formateur'|'etudiant'){ this.role=r;this.message='';this.error='';if(r==='etudiant')this.loadEtudiant(); }
  moyenne(v:number|null){return v===null?'—':v.toFixed(1)+'/20';}
}
