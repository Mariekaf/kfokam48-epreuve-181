import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { RelectureApiService } from '../../core/services/relecture-api.service';

@Component({
  selector: 'app-relecteur',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <main class="page">
      <a routerLink="/" class="back">← Retour à l'accueil</a>

      <section class="header">
        <span>ESPACE RELECTEUR</span>
        <h1>Rendre une relecture</h1>
        <p>Évaluez l'exercice avec une note entière sur 20 et un commentaire.</p>
      </section>

      <section class="card">
        <form [formGroup]="form" (ngSubmit)="envoyer()">
          <label>Identifiant de la relecture</label>
          <input type="number" formControlName="relectureId">

          <label>Note / 20</label>
          <input type="number" step="1" min="0" max="20" formControlName="note">

          <div class="validation"
               *ngIf="form.controls.note.touched && form.controls.note.invalid">
            La note doit être un entier compris entre 0 et 20.
          </div>

          <label>Commentaire</label>
          <textarea rows="6" formControlName="commentaire"
                    placeholder="Votre commentaire sur l'exercice..."></textarea>

          <button [disabled]="form.invalid || loading">
            {{ loading ? 'Envoi...' : 'Envoyer la relecture' }}
          </button>
        </form>

        <div class="success" *ngIf="success">{{ success }}</div>
        <div class="error" *ngIf="error">{{ error }}</div>
      </section>

      <p class="info">
        La liste automatique des relectures n'est pas disponible car l'endpoint GET correspondant
        n'est pas encore implémenté dans le backend.
      </p>
    </main>
  `,
  styles: [`
    :host{display:block;min-height:100vh;background:#F8FAFC;color:#1E293B}
    .page{max-width:700px;margin:auto;padding:45px 20px}
    .back{color:#2563EB;text-decoration:none}
    .header{margin:40px 0 25px}
    .header span{color:#2563EB;font-weight:700;font-size:12px}
    h1{color:#1E3A8A;font-size:34px;margin:8px 0}
    .header p,.info{color:#64748B}
    .card{background:white;border:1px solid #E2E8F0;border-radius:8px;padding:28px}
    form{display:grid;gap:10px}
    label{font-weight:600;font-size:14px;margin-top:8px}
    input,textarea{padding:12px;border:1px solid #CBD5E1;border-radius:6px;font:inherit;resize:vertical}
    input:focus,textarea:focus{outline:2px solid #DBEAFE;border-color:#2563EB}
    button{margin-top:14px;padding:12px;background:#2563EB;color:white;border:0;border-radius:6px;font-weight:600}
    button:disabled{opacity:.55}
    .validation{font-size:13px;color:#DC2626}
    .success,.error{margin-top:16px;padding:12px;border-radius:6px}
    .success{background:#F0FDF4;color:#166534}
    .error{background:#FEF2F2;color:#B91C1C}
    .info{font-size:13px;margin-top:18px}
  `]
})
export class Relecteur {
  private fb = inject(FormBuilder);
  private relectures = inject(RelectureApiService);

  loading = false;
  success = '';
  error = '';

  form = this.fb.nonNullable.group({
    relectureId: [1, [Validators.required, Validators.min(1)]],
    note: [0, [
      Validators.required,
      Validators.min(0),
      Validators.max(20),
      Validators.pattern(/^\d+$/)
    ]],
    commentaire: ['', Validators.required]
  });

  envoyer(): void {
    if (this.form.invalid) return;

    const valeur = this.form.getRawValue();

    if (!Number.isInteger(Number(valeur.note))) {
      this.error = 'La note doit être un entier.';
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    this.relectures.rendreRelecture(valeur.relectureId, {
      note: Number(valeur.note),
      commentaire: valeur.commentaire
    }).subscribe({
      next: () => {
        this.success = 'La relecture a été envoyée avec succès.';
        this.loading = false;
      },
      error: err => {
        this.error = err?.error?.message || err?.message || 'Impossible d’envoyer la relecture.';
        this.loading = false;
      }
    });
  }
}
