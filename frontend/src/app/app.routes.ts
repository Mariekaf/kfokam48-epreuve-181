import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Formateur } from './pages/formateur/formateur';
import { Etudiant } from './pages/etudiant/etudiant';
import { Relecteur } from './pages/relecteur/relecteur';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'formateur', component: Formateur },
  { path: 'etudiant', component: Etudiant },
  { path: 'relecteur', component: Relecteur },
  { path: '**', redirectTo: '' }
];
