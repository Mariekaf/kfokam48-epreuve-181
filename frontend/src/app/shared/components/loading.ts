import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/** Indicateur d'attente utilise pendant un appel API. */
@Component({
  selector: 'app-loading',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="loading" role="status" aria-live="polite">
      <span class="spinner" aria-hidden="true"></span>
      <span>{{ libelle() }}</span>
    </div>
  `,
  styles: `
    .loading {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 20px;
      color: var(--c-text-muted);
      font-size: 0.9rem;
    }

    .spinner {
      width: 18px;
      height: 18px;
      border: 2px solid var(--c-border);
      border-top-color: var(--c-primary);
      border-radius: 50%;
      animation: rotation 0.7s linear infinite;
      flex: none;
    }

    @keyframes rotation {
      to {
        transform: rotate(360deg);
      }
    }
  `
})
export class Loading {
  readonly libelle = input<string>('Chargement...');
}
