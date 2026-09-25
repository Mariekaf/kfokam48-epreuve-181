import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { PresenceApiService } from '../../core/services/presence-api.service';
import { ExerciceApiService } from '../../core/services/exercice-api.service';

@Component({
  selector: 'app-etudiant',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <main class="page">
      <a routerLink="/" class="back">← Retour à l'accueil</a>

      <header>
        <span>ESPACE ÉTUDIANT</span>
        <h1>Ma session</h1>
        <p>Marquez votre présence puis déposez votre exercice.</p>
      </header>

      <section class="identity">
        <label>Votre identifiant étudiant</label>
        <input type="number" [formControl]="etudiantId" placeholder="Ex. 1">
        <small>La sélection par nom n'est pas disponible dans le backend actuel.</small>
      </section>

      <div class="columns">
        <section class="card">
          <h2>Marquer ma présence</h2>
          <p class="muted">Saisissez le code communiqué par le formateur.</p>

          <form [formGroup]="presenceForm" (ngSubmit)="marquerPresence()">
            <label>Code de présence</label>
            <input formControlName="code" maxlength="20" placeholder="K7M4RX">
            <button [disabled]="presenceForm.invalid || etudiantId.invalid || presenceLoading">
              {{ presenceLoading ? 'Validation...' : 'Valider ma présence' }}
            </button>
          </form>

          <div class="success" *ngIf="presenceSuccess">{{ presenceSuccess }}</div>
          <div class="error" *ngIf="presenceError">{{ presenceError }}</div>
        </section>

        <section class="card">
          <h2>Déposer mon exercice</h2>

          <form [formGroup]="exerciceForm" (ngSubmit)="deposerExercice()">
            <label>Identifiant de la session</label>
            <input type="number" formControlName="sessionId" placeholder="1">

            <label>Lien de l'exercice</label>
            <input formControlName="lien" placeholder="https://github.com/...">

            <button [disabled]="exerciceForm.invalid || etudiantId.invalid || exerciceLoading">
              {{ exerciceLoading ? 'Dépôt...' : 'Déposer mon exercice' }}
            </button>
          </form>

          <div class="success" *ngIf="exerciceSuccess">{{ exerciceSuccess }}</div>
          <div class="error" *ngIf="exerciceError">{{ exerciceError }}</div>
        </section>
      </div>
    </main>
  `,
  styles: [`
    :host{display:block;min-height:100vh;background:#F8FAFC;color:#1E293B}
    .page{max-width:900px;margin:auto;padding:40px 20px}
    .back{color:#2563EB;text-decoration:none}
    header{margin:35px 0}
    header span{font-size:12px;font-weight:700;color:#2563EB}
    h1{color:#1E3A8A;font-size:34px;margin:8px 0}
    header p,.muted,small{color:#64748B}
    .identity,.card{background:white;border:1px solid #E2E8F0;border-radius:8px;padding:24px}
    .identity{display:grid;gap:8px;margin-bottom:20px}
    .columns{display:grid;grid-template-columns:1fr 1fr;gap:20px}
    form{display:grid;gap:10px}
    label{font-weight:600;font-size:14px;margin-top:8px}
    input{padding:12px;border:1px solid #CBD5E1;border-radius:6px;font:inherit}
    input:focus{outline:2px solid #DBEAFE;border-color:#2563EB}
    button{margin-top:10px;padding:12px;border:0;border-radius:6px;background:#2563EB;color:white;font-weight:600}
    button:disabled{opacity:.55}
    .success,.error{padding:12px;border-radius:6px;margin-top:15px}
    .success{background:#F0FDF4;color:#166534}
    .error{background:#FEF2F2;color:#B91C1C}
    @media(max-width:700px){.columns{grid-template-columns:1fr}}
  `]
})
export class Etudiant {
  private fb = inject(FormBuilder);
  private presences = inject(PresenceApiService);
  private exercices = inject(ExerciceApiService);

  presenceLoading = false;
  exerciceLoading = false;
  presenceSuccess = '';
  presenceError = '';
  exerciceSuccess = '';
  exerciceError = '';

  etudiantId = this.fb.nonNullable.control(1, [
    Validators.required,
    Validators.min(1)
  ]);

  presenceForm = this.fb.nonNullable.group({
    code: ['', Validators.required]
  });

  exerciceForm = this.fb.nonNullable.group({
    sessionId: [1, [Validators.required, Validators.min(1)]],
    lien: ['', [Validators.required, Validators.pattern(/^https?:\/\/.+/)]]
  });

  marquerPresence(): void {
    if (this.presenceForm.invalid || this.etudiantId.invalid) return;

    this.presenceLoading = true;
    this.presenceSuccess = '';
    this.presenceError = '';

    this.presences.marquerPresence({
      code: this.presenceForm.getRawValue().code,
      etudiantId: this.etudiantId.value
    }).subscribe({
      next: resultat => {
        this.presenceSuccess = 'Votre présence a été enregistrée avec succès.';
        this.exerciceForm.patchValue({ sessionId: resultat.sessionId });
        this.presenceLoading = false;
      },
      error: err => {
        this.presenceError = err?.error?.message || err?.message || 'Impossible d’enregistrer la présence.';
        this.presenceLoading = false;
      }
    });
  }

  deposerExercice(): void {
    if (this.exerciceForm.invalid || this.etudiantId.invalid) return;

    this.exerciceLoading = true;
    this.exerciceSuccess = '';
    this.exerciceError = '';

    const form = this.exerciceForm.getRawValue();

    this.exercices.deposerExercice({
      sessionId: form.sessionId,
      etudiantId: this.etudiantId.value,
      lien: form.lien
    }).subscribe({
      next: () => {
        this.exerciceSuccess = 'Votre exercice a été déposé avec succès.';
        this.exerciceLoading = false;
      },
      error: err => {
        this.exerciceError = err?.error?.message || err?.message || 'Impossible de déposer l’exercice.';
        this.exerciceLoading = false;
      }
    });
  }
}
