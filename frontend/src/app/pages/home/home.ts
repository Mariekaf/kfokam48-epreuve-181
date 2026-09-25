import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  template: `
    <main class="page">
      <section class="hero">
        <span class="eyebrow">KFOKAM48</span>
        <h1>Plateforme de suivi des sessions</h1>
        <p>Gestion des présences, exercices et relectures.</p>
      </section>

      <section class="roles">
        <a routerLink="/formateur" class="role-card">
          <div class="icon">F</div>
          <h2>Formateur</h2>
          <p>Ouvrir une session et suivre les étudiants.</p>
          <span>Accéder à l'espace →</span>
        </a>

        <a routerLink="/etudiant" class="role-card">
          <div class="icon">E</div>
          <h2>Étudiant</h2>
          <p>Marquer sa présence et déposer son exercice.</p>
          <span>Accéder à l'espace →</span>
        </a>

        <a routerLink="/relecteur" class="role-card">
          <div class="icon">R</div>
          <h2>Relecteur</h2>
          <p>Évaluer un exercice avec une note et un commentaire.</p>
          <span>Accéder à l'espace →</span>
        </a>
      </section>
    </main>
  `,
  styles: [`
    :host { display:block; min-height:100vh; background:#F8FAFC; color:#1E293B; }
    .page { max-width:1100px; margin:auto; padding:80px 24px; }
    .hero { text-align:center; max-width:700px; margin:0 auto 55px; }
    .eyebrow { color:#2563EB; font-weight:700; letter-spacing:.08em; }
    h1 { font-size:42px; margin:12px 0; color:#1E3A8A; }
    .hero p { color:#64748B; font-size:18px; }
    .roles { display:grid; grid-template-columns:repeat(3,1fr); gap:20px; }
    .role-card {
      background:#fff; border:1px solid #E2E8F0; border-radius:8px;
      padding:30px; text-decoration:none; color:#1E293B;
      transition:.2s ease;
    }
    .role-card:hover { border-color:#2563EB; transform:translateY(-2px); }
    .icon {
      width:46px; height:46px; display:flex; align-items:center; justify-content:center;
      background:#DBEAFE; color:#1E3A8A; border-radius:8px; font-weight:700;
    }
    h2 { margin:20px 0 8px; }
    .role-card p { color:#64748B; min-height:48px; }
    .role-card span { display:block; margin-top:24px; color:#2563EB; font-weight:600; }
    @media(max-width:800px){ .roles{grid-template-columns:1fr;} h1{font-size:32px;} }
  `]
})
export class Home {}
