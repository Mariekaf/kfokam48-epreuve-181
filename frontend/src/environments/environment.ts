/**
 * Configuration unique de l'environnement de developpement.
 *
 * L'application appelle toujours des URL relatives `/api/...`.
 * Ces URL sont redirigees vers le backend par `proxy.conf.json`,
 * declare dans `angular.json` (cible : `serve.options.proxyConfig`).
 *
 * Aucun appel HTTP ne doit contenir une URL absolue en dur.
 */
export const environment = {
  production: false,
  apiBaseUrl: '/api'
};
