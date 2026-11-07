// Development (ng serve): the browser calls the backend on its own port, so the backend must allow
// this app's origin (cors.allowed-origins in application.properties).
export const environment = {
  apiUrl: 'http://localhost:8080/api',
};
