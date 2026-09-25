import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/** En-tete de page : titre, sous-titre et zone d'action. */
@Component({
  selector: 'app-page-header',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <header class="page-header">
      <div class="page-header-text">
        <h1>{{ titre() }}</h1>
        @if (sousTitre()) {
          <p class="muted">{{ sousTitre() }}</p>
        }
      </div>
      @if (hasAction()) {
        <div class="page-header-action">
          <ng-content select="[header-action]" />
        </div>
      }
    </header>
  `,
  styles: `
    .page-header {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      gap: 16px;
      flex-wrap: wrap;
      margin-bottom: 24px;
    }

    .page-header-text h1 {
      margin: 0;
      font-size: 1.45rem;
    }

    .page-header-text p {
      margin: 4px 0 0;
      font-size: 0.92rem;
    }

    @media (max-width: 640px) {
      .page-header-action {
        width: 100%;
      }
    }
  `
})
export class PageHeader {
  readonly titre = input.required<string>();
  readonly sousTitre = input<string | null>(null);
  readonly hasAction = input<boolean>(false);
}
