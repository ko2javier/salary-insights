// Production build (Docker): nginx serves the app and forwards /api/ to the backend on the same
// origin, so there is no CORS involved.
export const environment = {
  apiUrl: '/api',
};
