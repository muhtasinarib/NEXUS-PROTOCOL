# Stage 1: Build the Next.js app
FROM node:20-alpine AS builder
WORKDIR /app

# Install dependencies needed for node-gyp or prisma
RUN apk add --no-cache libc6-compat openssl

COPY package*.json ./
RUN npm ci

COPY . .

# Generate Prisma Client code
ENV NEXT_TELEMETRY_DISABLED 1
RUN npx prisma generate

# Build NextJS production bundle
RUN npm run build

# Stage 2: Runner container
FROM node:20-alpine AS runner
WORKDIR /app

ENV NODE_ENV production
ENV PORT 3000
ENV HOSTNAME "0.0.0.0"

# Install production dependencies only
COPY package*.json ./
RUN npm ci --only=production

COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/prisma ./prisma

EXPOSE 3000

CMD ["npx", "prisma", "db", "push", "&&", "npm", "start"]
