# ============================
# Stage 1: Build React app
# ============================
FROM node:18-alpine AS build

WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install

# Copy source and build
COPY . .
RUN npm run build


# ============================
# Stage 2: Serve with Nginx
# ============================
FROM nginx:alpine

# Use custom Nginx config (proxy + SPA)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built React app
COPY --from=build /app/build /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
