import { ChangeDetectionStrategy, Component, input } from '@angular/core';

export type TypeAlerte = 'success' | 'error' | 'warning' | 'info';

/**
 * Bandeau de message unifie pour les retours de l'API et les
 * informations fonctionnelles. Aucune fenetre `alert()` n'est utilisee.
 */
@Component({
  selector: 'app-alert-message',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (message()) {
      <div
        class="alert"
        [class]="'alert alert-' + type()"
        role="status"
        [attr.aria-live]="type() === 'error' ? 'assertive' : 'polite'"
      >
        <div class="alert-body">
          @if (titre()) {
            <div class="alert-title">{{ titre() }}</div>
          }
          <div>{{ message() }}</div>
          @if (detail()) {
            <div class="text-sm" style="margin-top: 6px; opacity: 0.85">
              {{ detail() }}
            </div>
          }
        </div>
      </div>
    }
  `
})
export class AlertMessage {
  readonly type = input<TypeAlerte>('info');
  readonly titre = input<string | null>(null);
  readonly message = input<string | null>(null);
  readonly detail = input<string | null>(null);
}
