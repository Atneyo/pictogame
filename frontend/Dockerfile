# ---------- Stage 1: build the static files ----------
FROM node:24-alpine AS build
WORKDIR /app
# Copy the manifests first: the dependency layer is cached until they change
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
# Runs the TypeScript check, then bundles the app into /app/dist
RUN npm run build

# ---------- Stage 2: nginx serves the result ----------
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80