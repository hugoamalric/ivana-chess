# Ivana Chess - Webapp

Web application.

## How to build

```bash
./gradlew :ivana-chess-webapp:assemble
```

## How to test

```bash
./gradlew :ivana-chess-webapp:check
```

## How to run

### With Gradle

```bash
./gradlew :ivana-chess-webapp:serve
```

### With Docker

```bash
docker run -p 80:80 -e 'API_BASE_URL=http://localhost:4200' gleroy/ivana-chess-webapp
```

## Configuration

You can edit `assets/env.js` to override configuration.

|  Property  | Environment variable |  Description |     Default value     |
|:----------:|:--------------------:|:------------:|:---------------------:|
| apiBaseUrl |     API_BASE_URL     | API base URL | http://localhost:8080 |
