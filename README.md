# Ivana Chess

Chess game.

## How to build

```bash
./gradlew assemble
```

## How to test

```bash
./gradlew check
```

## How to run API

### With Gradle

```bash
docker-compose -f api/docker-compose-dev.yml up -d
./gradlew bootRun
```

### With Docker

```bash
docker-compose -f api/docker-compose.yml up -d
```

# How to run webapp

### With Gradle

```bash
./gradlew serve
```

### With Docker

```bash
docker run -p 80:80 -e 'API_BASE_URL=http://localhost:4200' gleroy/ivana-chess-webapp
```

## API documentation

[API documentation](https://documenter.getpostman.com/view/9866325/TW6tLq59)
