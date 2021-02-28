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
docker-compose -f api/docker-compose.yml up -d
```

## Configuration

You can override configuration by setting JVM properties or environment variables.

|               Property               |         Environment variable         |                   Description                  |     Default value    |
|:------------------------------------:|:------------------------------------:|:----------------------------------------------:|:--------------------:|
|    ivana-chess.server.bind-address   |       IVANA_CHESS_BIND_ADDRESS       |               Server bind address              |        0.0.0.0       |
|        ivana-chess.server.port       |           IVANA_CHESS_PORT           |                   Server port                  |         8080         |
|    ivana-chess.server.context-path   |       IVANA_CHESS_CONTEXT_PATH       |                  Context path                  |           /          |
|  ivana-chess.server.allowed-origins  |      IVANA_CHESS_ALLOWED_ORIGINS     |     Coma-separated list of allowed origins     |           -          |
|          ivana-chess.db.host         |          IVANA_CHESS_DB_HOST         |                Host of database                |       127.0.0.1      |
|          ivana-chess.db.port         |          IVANA_CHESS_DB_PORT         |                Port of database                |         5432         |
|          ivana-chess.db.name         |          IVANA_CHESS_DB_NAME         |                Name of database                |     ivanachessapi    |
|         ivana-chess.db.schema        |         IVANA_CHESS_DB_SCHEMA        |                 Name of schema                 |        public        |
|        ivana-chess.db.username       |        IVANA_CHESS_DB_USERNAME       |      Username used to connect to database      |     ivanachessapi    |
|        ivana-chess.db.password       |        IVANA_CHESS_DB_PASSWORD       |      Password used to connect to database      |     ivanachessapi    |
|        ivana-chess.auth.secret       |        IVANA_CHESS_AUTH_SECRET       |           Secret used to generate JWT          |       changeit       |
|       ivana-chess.auth.validity      |      IVANA_CHESS_AUTH_EXPIRATION     |  Number of seconds for which the JWT is valid  |        604800        |
|     ivana-chess.auth.header.name     |     IVANA_CHESS_AUTH_HEADER_NAME     |       HTTP header name which contains JWT      |     Authorization    |
| ivana-chess.auth.header.value-prefix | IVANA_CHESS_AUTH_HEADER_VALUE_PREFIX | Prefix of HTTP header value which prefixes JWT |        Bearer        |
|     ivana-chess.auth.cookie.name     |     IVANA_CHESS_AUTH_COOKIE_NAME     |         Name of cookie used to send JWT        | _ivana_chess_session |
|    ivana-chess.auth.cookie.domain    |    IVANA_CHESS_AUTH_COOKIE_DOMAIN    |                Domain of cookie                |       localhost      |
|    ivana-chess.auth.cookie.secure    |    IVANA_CHESS_AUTH_COOKIE_SECURE    |      If cookie secure attribute is enabled     |         false        |
|   ivana-chess.auth.cookie.http-only  |   IVANA_CHESS_AUTH_COOKIE_HTTP_ONLY  |    If cookie http only attribute is enabled    |         true         |
