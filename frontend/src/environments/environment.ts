// Where the backend lives. The browser calls it directly, so the backend must allow this app's
// origin (cors.allowed-origins in application.properties).
export const environment = {
  apiUrl: 'http://localhost:8080',
};
