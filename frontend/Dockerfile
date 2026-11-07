# Stage 1: build Angular for production (environment.production.ts → apiUrl '/api')
FROM node:22-alpine AS build
WORKDIR /workspace

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npx ng build

# Stage 2: nginx serves the static files and forwards /api/ to the backend
FROM nginx:1.28-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/dist/frontend/browser /usr/share/nginx/html
EXPOSE 80
