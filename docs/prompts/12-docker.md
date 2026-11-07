# 12 — Docker

## 2026-11-07

> Vamos con Docker. El objetivo: cualquiera que clone el repo ejecuta `docker compose up --build` y tiene la app entera en `http://localhost:8095`, sin instalar Java ni Node y sin ningún secreto.
>
> 1. `backend/Dockerfile` en dos fases (compilación con Gradle; ejecución con JRE 21). Usuario sin root. Crea `/data` y dáselo a ese usuario, o SQLite fallará con `SQLITE_CANTOPEN`.
> 2. `frontend/Dockerfile` en dos fases (compilación con Node; servido con nginx).
> 3. `frontend/nginx.conf`: sirve Angular, redirige las rutas desconocidas a `index.html` y reenvía `/api/` al backend (`http://backend:8080`). Comprueba si el backend espera el prefijo `/api` o no, y ajústalo.
> 4. `environment.production.ts` con `apiUrl: '/api'`. En producción no hace falta CORS; `CORS_ALLOWED_ORIGINS` queda solo para desarrollo.
> 5. `docker-compose.yml`:
>    * `frontend`: `ports: "127.0.0.1:${FRONTEND_PORT:-8095}:80"`.
>    * `backend`: sin `ports`. Volumen con nombre en `/data`, `DB_PATH=/data/salary.db`, `mem_limit: 512m` y `-Xmx384m`.
>    * Los dos con `restart: unless-stopped`.
> 6. Healthcheck del backend con `/actuator/health`, y que el frontend espere a que esté sano.
> 7. Pruébalo: borra el volumen, `docker compose up --build`, comprueba que el seed carga 10.000 y que la lista y los insights funcionan a través de `localhost:8095`. Reinicia y comprueba que no duplica.
> 8. `docs/deployment.md`: cómo desplegar en un VPS detrás de un proxy inverso con autenticación básica, de forma genérica, sin mi dominio ni contraseñas.
>
> Después, en otro commit: CI con GitHub Actions que ejecute los tests del backend, los del frontend y el build de Angular en cada push. CD no: queda en “Mejoras posibles”.

*"Let's go with Docker. The goal: anyone who clones the repo runs `docker compose up --build` and has the whole app at `http://localhost:8095`, without installing Java or Node and without any secret.*

*1. `backend/Dockerfile` in two stages (build with Gradle; run with JRE 21). Non-root user. Create `/data` and give it to that user, or SQLite will fail with `SQLITE_CANTOPEN`.*
*2. `frontend/Dockerfile` in two stages (build with Node; served with nginx).*
*3. `frontend/nginx.conf`: serves Angular, sends unknown routes to `index.html` and forwards `/api/` to the backend (`http://backend:8080`). Check whether the backend expects the `/api` prefix or not, and adjust.*
*4. `environment.production.ts` with `apiUrl: '/api'`. In production CORS isn't needed; `CORS_ALLOWED_ORIGINS` stays for development only.*
*5. `docker-compose.yml`: `frontend` with `ports: "127.0.0.1:${FRONTEND_PORT:-8095}:80"`; `backend` without `ports`, a named volume at `/data`, `DB_PATH=/data/salary.db`, `mem_limit: 512m` and `-Xmx384m`; both with `restart: unless-stopped`.*
*6. Backend healthcheck on `/actuator/health`, and the frontend waits until it is healthy.*
*7. Test it: delete the volume, `docker compose up --build`, check that the seed loads 10,000 and that the list and the insights work through `localhost:8095`. Restart and check it doesn't duplicate.*
*8. `docs/deployment.md`: how to deploy on a VPS behind a reverse proxy with basic authentication, generically, without my domain or passwords.*

*Then, in another commit: CI with GitHub Actions running the backend tests, the frontend tests and the Angular build on every push. No CD: it goes under "Possible improvements"."*

> si has el push

*"Yes, do the push."*
