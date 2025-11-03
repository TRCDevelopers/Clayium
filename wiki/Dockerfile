FROM oven/bun:1 AS base
WORKDIR /app

COPY package.json bun.lockb* ./
RUN bun install

FROM base AS dev
EXPOSE 5173
CMD ["bun", "run", "docs:dev", "--host", "0.0.0.0"]
