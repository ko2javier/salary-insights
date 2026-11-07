# Deployment

## Run it locally

Only Docker is needed: no Java, no Node, no secrets.

```bash
git clone <repository-url> salary-insights
cd salary-insights
docker compose up --build
```

Open **http://localhost:8095**. The first start loads 10,000 employees (a few seconds) before the
backend reports healthy; the frontend waits for it.

| What | Where |
|---|---|
| App | `http://localhost:8095` (change it with `FRONTEND_PORT=9000 docker compose up --build`) |
| API | the same origin, under `/api/` (nginx forwards it to the backend) |
| Data | named volume `salary-data`, file `/data/salary.db` inside the backend container |

Useful commands:

```bash
docker compose ps                   # both services, backend should say "healthy"
docker compose logs -f backend      # "Seeded 10000 employees" on the first start only
docker compose down                 # stop, keep the data
docker compose down -v              # stop and delete the data: the next start seeds again
```

### How the pieces fit

```
browser ──► nginx (frontend container, port 80, published on 127.0.0.1:8095)
              ├── /         Angular files; unknown paths fall back to index.html
              └── /api/     proxied to http://backend:8080/api/  (internal network only)
                              └── Spring Boot + SQLite on the volume
```

- The backend publishes **no port**: only nginx can reach it. Swagger UI and `/actuator` are
  therefore not reachable from outside the containers.
- The app and the API share one origin, so CORS plays no part in Docker. `CORS_ALLOWED_ORIGINS`
  is only for development, when `ng serve` (port 4200) calls the backend on port 8080.
- The backend runs as a non-root user, with `mem_limit: 512m` and a 384 MB heap.

## Deploy on a VPS behind a reverse proxy

The app has no login of its own. On a server reachable from the internet, put it behind a reverse
proxy that adds **TLS** and **basic authentication**. The compose file already binds the app to
`127.0.0.1`, so the only way in from outside is through that proxy.

### 1. Start the app on the server

```bash
git clone <repository-url> salary-insights
cd salary-insights
docker compose up --build -d
curl -I http://127.0.0.1:8095     # 200 from the server itself
```

If port 8095 is taken, choose another one with `FRONTEND_PORT` and use it in the proxy below.

### 2. Point a domain at the server

Create an `A` record (and `AAAA` for IPv6) for your domain, for example `salary.example.com`,
pointing at the server's public IP. Open ports 80 and 443 in the firewall; nothing else is needed
for the app.

### 3a. Option A: Caddy (TLS certificates are automatic)

Create a password hash (it is never stored in plain text):

```bash
caddy hash-password            # type the password, copy the $2a$... hash
```

`/etc/caddy/Caddyfile`:

```caddyfile
salary.example.com {
    basic_auth {
        hr-manager <paste-the-hash-here>
    }
    reverse_proxy 127.0.0.1:8095
}
```

```bash
sudo systemctl reload caddy
```

### 3b. Option B: nginx with Certbot

```bash
sudo apt install nginx apache2-utils certbot python3-certbot-nginx
sudo htpasswd -B -c /etc/nginx/.htpasswd-salary hr-manager    # asks for the password
```

`/etc/nginx/sites-available/salary`:

```nginx
server {
    listen 80;
    server_name salary.example.com;

    auth_basic           "Salary Insights";
    auth_basic_user_file /etc/nginx/.htpasswd-salary;

    location / {
        proxy_pass http://127.0.0.1:8095;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
sudo ln -s /etc/nginx/sites-available/salary /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl reload nginx
sudo certbot --nginx -d salary.example.com      # adds TLS and the redirect to https
```

### 4. Check it

```bash
curl -I https://salary.example.com                  # 401: the password is required
curl -I -u hr-manager https://salary.example.com    # 200 after typing the password
```

From another machine, `http://<server-ip>:8095` must **not** answer: the port only listens on
`127.0.0.1`. To confirm on the server, `ss -tlnp | grep 8095` should show `127.0.0.1:8095`, never
`0.0.0.0:8095`.

## Update to a new version

```bash
cd salary-insights
git pull
docker compose up --build -d     # rebuilds the images; the volume and its data stay
```

## Back up and restore the data

The whole database is one SQLite file in the `salary-data` volume.

```bash
# Back up (stop first so the file is not copied halfway through a write)
docker compose stop backend
docker run --rm -v salary-insights_salary-data:/data -v "$PWD":/backup alpine \
  cp /data/salary.db /backup/salary-$(date +%F).db
docker compose start backend

# Restore
docker compose stop backend
docker run --rm -v salary-insights_salary-data:/data -v "$PWD":/backup alpine \
  sh -c 'cp /backup/salary-YYYY-MM-DD.db /data/salary.db && chown 100:101 /data/salary.db'
docker compose start backend
```

The volume name starts with the folder the repository was cloned into (`salary-insights_` above);
`docker volume ls` shows the exact name. The `chown` gives the file back to the backend's non-root
user.
