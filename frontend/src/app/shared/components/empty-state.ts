import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/** Etat vide : liste sans donnee, action indisponible, etc. */
@Component({
  selector: 'app-empty-state',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="empty">
      <div class="empty-title">{{ titre() }}</div>
      @if (description()) {
        <p class="empty-description">{{ description() }}</p>
      }
      <ng-content />
    </div>
  `,
  styles: `
    .empty {
      padding: 40px 24px;
      text-align: center;
    }

    .empty-title {
      color: var(--c-text);
      font-size: 1rem;
      font-weight: 650;
    }

    .empty-description {
      max-width: 560px;
      margin: 8px auto 0;
      color: var(--c-text-muted);
      font-size: 0.9rem;
    }
  `
})
export class EmptyState {
  readonly titre = input<string>('Aucune donnée');
  readonly description = input<string | null>(null);
}
