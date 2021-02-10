# Ivana Chess - API

API.

## How to build

```bash
./gradlew :ivana-chess-api:assemble
```

## How to test

```bash
./gradlew :ivana-chess-api:check
```

## How to run

### With Gradle

```bash
./gradlew :ivana-chess-api:bootRun
```

### With Docker

```bash
docker -p 8080:8080 run gleroy/ivana-chess-api
```

## Configuration

You can override configuration by setting JVM properties or environment variables.

|              Property              |   Environment variable   |               Description              | Default value |
|:----------------------------------:|:------------------------:|:--------------------------------------:|:-------------:|
|   ivana-chess.server.bind-address  | IVANA_CHESS_BIND_ADDRESS |           Server bind address          |    0.0.0.0    |
|       ivana-chess.server.port      |     IVANA_CHESS_PORT     |               Server port              |      8080     |
| ivana-chess.server.allowed-origins |   IVANA_ALLOWED_ORIGINS  | Coma-separated list of allowed origins |       -       |
