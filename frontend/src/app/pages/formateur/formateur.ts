import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { SessionApiService } from '../../core/services/session-api.service';
import { TableauApiService } from '../../core/services/tableau-api.service';

@Component({
  selector: 'app-formateur',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="layout">
      <aside>
        <div class="brand">KFOKAM48</div>
        <nav>
          <a class="active">Espace formateur</a>
          <a routerLink="/">Accueil</a>
        </nav>
      </aside>

      <main>
        <header>
          <div>
            <span class="label">FORMATEUR</span>
            <h1>Gestion des sessions</h1>
            <p>Ouvrez une session et consultez le suivi d'une promotion.</p>
          </div>
        </header>

        <div class="grid">
          <section class="card">
            <h2>Ouvrir une session</h2>

            <form [formGroup]="sessionForm" (ngSubmit)="ouvrirSession()">
              <label>Titre de la session</label>
              <input formControlName="titre" placeholder="Ex. Angular - Séance 1">

              <label>Identifiant de la promotion</label>
              <input type="number" formControlName="promotionId" placeholder="1">

              <button type="submit" [disabled]="sessionForm.invalid || sessionLoading">
                {{ sessionLoading ? 'Ouverture...' : 'Ouvrir la session' }}
              </button>
            </form>

            <div class="error" *ngIf="sessionError">{{ sessionError }}</div>

            <div class="session-result" *ngIf="session">
              <span>Code de présence</span>
              <strong>{{ session.code }}</strong>
              <small>Ouverture : {{ session.ouvertureAt | date:'short' }}</small>
              <small>Expiration : {{ session.expirationAt | date:'short' }}</small>
            </div>
          </section>

          <section class="card">
            <h2>Tableau de suivi</h2>

            <form [formGroup]="tableauForm" (ngSubmit)="chargerTableau()" class="inline">
              <div>
                <label>Promotion</label>
                <input type="number" formControlName="promotionId" placeholder="1">
              </div>
              <button type="submit" [disabled]="tableauForm.invalid || tableauLoading">
                {{ tableauLoading ? 'Chargement...' : 'Afficher' }}
              </button>
            </form>

            <div class="error" *ngIf="tableauError">{{ tableauError }}</div>

            <div class="empty" *ngIf="!tableauLoading && tableauCharge && tableau.length === 0">
              Aucun étudiant à afficher.
            </div>

            <div class="table-wrap" *ngIf="tableau.length">
              <table>
                <thead>
                  <tr>
                    <th>Étudiant</th>
                    <th>Présences</th>
                    <th>Exercices</th>
                    <th>Moyenne</th>
                    <th>Relectures en attente</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let ligne of tableau">
                    <td>{{ ligne.nom }}</td>
                    <td>{{ ligne.presences }}</td>
                    <td>{{ ligne.exercicesDeposes }}</td>
                    <td>{{ ligne.moyenne === null ? '—' : ligne.moyenne + '/20' }}</td>
                    <td>{{ ligne.relecturesEnAttente }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </section>
        </div>
      </main>
    </div>
  `,
  styles: [`
    :host{display:block;background:#F8FAFC;min-height:100vh;color:#1E293B}
    .layout{display:grid;grid-template-columns:230px 1fr;min-height:100vh}
    aside{background:#1E3A8A;color:white;padding:28px 18px}
    .brand{font-size:20px;font-weight:800;margin-bottom:45px}
    nav{display:flex;flex-direction:column;gap:8px}
    nav a{padding:12px;border-radius:6px;color:#DBEAFE;text-decoration:none}
    nav .active{background:#2563EB;color:white}
    main{padding:38px}
    header{margin-bottom:28px}
    .label{color:#2563EB;font-weight:700;font-size:12px}
    h1{margin:6px 0;font-size:30px;color:#1E3A8A}
    header p{color:#64748B}
    .grid{display:grid;gap:24px}
    .card{background:white;border:1px solid #E2E8F0;border-radius:8px;padding:26px}
    h2{margin-top:0}
    form{display:grid;gap:10px}
    label{font-size:14px;font-weight:600;margin-top:8px}
    input{padding:12px;border:1px solid #CBD5E1;border-radius:6px;font:inherit}
    input:focus{outline:2px solid #DBEAFE;border-color:#2563EB}
    button{background:#2563EB;color:white;border:0;border-radius:6px;padding:12px 18px;font-weight:600;cursor:pointer}
    button:disabled{opacity:.55;cursor:not-allowed}
    .session-result{margin-top:20px;background:#EFF6FF;border:1px solid #BFDBFE;padding:22px;border-radius:8px;text-align:center}
    .session-result span,.session-result small{display:block;color:#64748B}
    .session-result strong{display:block;font-size:36px;letter-spacing:5px;color:#1E3A8A;margin:8px}
    .error{margin-top:14px;padding:12px;background:#FEF2F2;color:#B91C1C;border-radius:6px}
    .empty{padding:20px;color:#64748B;text-align:center}
    .inline{grid-template-columns:1fr auto;align-items:end;margin-bottom:20px}
    .inline div{display:grid;gap:6px}
    .table-wrap{overflow-x:auto}
    table{width:100%;border-collapse:collapse}
    th{background:#EFF6FF;text-align:left;color:#1E3A8A}
    th,td{padding:13px;border-bottom:1px solid #E2E8F0}
    @media(max-width:760px){.layout{grid-template-columns:1fr}aside{padding:16px}.brand{margin-bottom:15px}nav{flex-direction:row}main{padding:22px 15px}.inline{grid-template-columns:1fr}}
  `]
})
export class Formateur {
  private fb = inject(FormBuilder);
  private sessions = inject(SessionApiService);
  private tableaux = inject(TableauApiService);

  session: any = null;
  tableau: any[] = [];

  sessionLoading = false;
  tableauLoading = false;
  tableauCharge = false;

  sessionError = '';
  tableauError = '';

  sessionForm = this.fb.nonNullable.group({
    titre: ['', Validators.required],
    promotionId: [1, [Validators.required, Validators.min(1)]]
  });

  tableauForm = this.fb.nonNullable.group({
    promotionId: [1, [Validators.required, Validators.min(1)]]
  });

  ouvrirSession(): void {
    if (this.sessionForm.invalid) return;

    this.sessionLoading = true;
    this.sessionError = '';

    this.sessions.ouvrirSession(this.sessionForm.getRawValue()).subscribe({
      next: resultat => {
        this.session = resultat;
        this.sessionLoading = false;
      },
      error: err => {
        this.sessionError = err?.error?.message || err?.message || 'Impossible d’ouvrir la session.';
        this.sessionLoading = false;
      }
    });
  }

  chargerTableau(): void {
    if (this.tableauForm.invalid) return;

    this.tableauLoading = true;
    this.tableauError = '';
    this.tableauCharge = true;

    this.tableaux.consulterTableau(this.tableauForm.getRawValue().promotionId).subscribe({
      next: resultat => {
        this.tableau = resultat;
        this.tableauLoading = false;
      },
      error: err => {
        this.tableau = [];
        this.tableauError = err?.error?.message || err?.message || 'Impossible de charger le tableau.';
        this.tableauLoading = false;
      }
    });
  }
}
