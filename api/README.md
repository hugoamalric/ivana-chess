# Ivana Chess - API

API.

## How to build

```bash
./gradlew :ivana-chess-api:assemble
```

## How to test

```bash
./gradlew dockerComposeUp :ivana-chess-api:check
```

## How to run

```bash
./gradlew :ivana-chess-api:bootRun
```

By default, the application will run with `dev` profile, but you can override it with property `ivana-chess-api.profile`
.

Available profiles:

- `dev`: used to local development;

## Configuration

You can override configuration by setting JVM properties or environment variables.

|                    Property                   |            Environment variable            |                          Description                         |                             Default value                             |
|:---------------------------------------------:|:------------------------------------------:|:------------------------------------------------------------:|:---------------------------------------------------------------------:|
|        ivana-chess.server.bind-address        |       IVANA_CHESS_SERVER_BIND_ADDRESS      |                      Server bind address                     |                                0.0.0.0                                |
|            ivana-chess.server.port            |           IVANA_CHESS_SERVER_PORT          |                          Server port                         |                                  8080                                 |
|        ivana-chess.server.context-path        |       IVANA_CHESS_SERVER_CONTEXT_PATH      |                         Context path                         |                                   /                                   |
|       ivana-chess.server.allowed-origins      |     IVANA_CHESS_SERVER_ALLOWED_ORIGINS     |            Coma-separated list of allowed origins            |                                   -                                   |
|         ivana-chess.server.ssl.enabled        |       IVANA_CHESS_SERVER_SSL_ENABLED       |                       If SSL is enabled                      |                                 false                                 |
|        ivana-chess.server.ssl.keystore        |       IVANA_CHESS_SERVER_SSL_KEYSTORE      |                     Path to keystore file                    |                                                                       |
|      ivana-chess.server.ssl.keystore-type     |    IVANA_CHESS_SERVER_SSL_KEYSTORE_TYPE    |                       Type of keystore                       |                                 PKCS12                                |
|    ivana-chess.server.ssl.keystore-password   |  IVANA_CHESS_SERVER_SSL_KEYSTORE_PASSWORD  |                     Password of keystore                     |                                changeit                               |
|        ivana-chess.server.ssl.key-alias       |      IVANA_CHESS_SERVER_SSL_KEY_ALIAS      |                   Alias of key in keystore                   |                               localhost                               |
|       ivana-chess.server.ssl.truststore       |      IVANA_CHESS_SERVER_SSL_TRUSTSTORE     |                    Path to truststore file                   |                                                                       |
|     ivana-chess.server.ssl.truststore-type    |   IVANA_CHESS_SERVER_SSL_TRUSTSTORE_TYPE   |                      Type of truststore                      |                                 PKCS12                                |
|   ivana-chess.server.ssl.truststore-password  | IVANA_CHESS_SERVER_SSL_TRUSTSTORE_PASSWORD |                    Password of truststore                    |                                changeit                               |
|               ivana-chess.db.url              |             IVANA_CHESS_DB_URL             |                     JDBC URL of database                     | jdbc:postgresql://127.0.0.1:5432/ivana_chess_api?currentSchema=public |
|            ivana-chess.db.username            |           IVANA_CHESS_DB_USERNAME          |             Username used to connect to database             |                            ivana_chess_api                            |
|            ivana-chess.db.password            |           IVANA_CHESS_DB_PASSWORD          |             Password used to connect to database             |                            ivana_chess_api                            |
|            ivana-chess.broker.host            |           IVANA_CHESS_BROKER_URL           |                        Host of broker                        |                               127.0.0.1                               |
|            ivana-chess.broker.port            |           IVANA_CHESS_BROKER_PORT          |                        Port of broker                        |                                  5672                                 |
|            ivana-chess.broker.vhost           |          IVANA_CHESS_BROKER_VHOST          |                    Virtual host to connect                   |                                   /                                   |
|          ivana-chess.broker.username          |         IVANA_CHESS_BROKER_USERNAME        |              Username used to connect to broker              |                                 guest                                 |
|          ivana-chess.broker.password          |         IVANA_CHESS_BROKER_PASSWORD        |              Password used to connect to broker              |                                 guest                                 |
|         ivana-chess.broker.match-queue        |           IVANA_CHESS_MATCH_QUEUE          |                      Name of match queue                     |                                 match                                 |
|      ivana-chess.broker.matchmaking-queue     |        IVANA_CHESS_MATCHMAKING_QUEUE       |                   Name of matchmaking queue                  |                              matchmaking                              |
| ivana-chess.broker.matchmaking-leave-exchange |   IVANA_CHESS_MATCHMAKING_LEAVE_EXCHANGE   |              Name of matchmaking leave exchange              |                           matchmaking-leave                           |
|  ivana-chess.broker.matchmaking-instances-ids |    IVANA_CHESS_MATCHMAKING_INSTANCES_IDS   | Coma-separated list of ivana-chess-matchmaking instances IDs |                       ivana-chess-matchmaking-01                      |
|         ivana-chess.broker.ssl.enabled        |       IVANA_CHESS_BROKER_SSL_ENABLED       |            If SSL is enabled for broker connection           |                                 false                                 |
|     ivana-chess.broker.ssl.verify-hostname    |   IVANA_CHESS_BROKER_SSL_VERIFY_HOSTNAME   |       If certificate hostname is verified on connection      |                                 false                                 |
|             ivana-chess.stomp.host            |            IVANA_CHESS_STOMP_URL           |                         Host of STOMP                        |                               127.0.0.1                               |
|             ivana-chess.stomp.port            |           IVANA_CHESS_STOMP_PORT           |                         Port of STOMP                        |                                 61613                                 |
|            ivana-chess.stomp.vhost            |           IVANA_CHESS_STOMP_VHOST          |                    Virtual host to connect                   |                                   /                                   |
|           ivana-chess.stomp.username          |         IVANA_CHESS_STOMP_USERNAME         |               Username used to connect to STOMP              |                                 guest                                 |
|           ivana-chess.stomp.password          |         IVANA_CHESS_STOMP_PASSWORD         |               Password used to connect to STOMP              |                                 guest                                 |
|         ivana-chess.stomp.ssl-enabled         |        IVANA_CHESS_STOMP_SSL_ENABLED       |            If SSL is enabled for STOMP connection            |                                 false                                 |
|            ivana-chess.auth.secret            |           IVANA_CHESS_AUTH_SECRET          |                  Secret used to generate JWT                 |                                changeit                               |
|           ivana-chess.auth.validity           |         IVANA_CHESS_AUTH_EXPIRATION        |         Number of seconds for which the JWT is valid         |                                 604800                                |
|          ivana-chess.auth.header.name         |        IVANA_CHESS_AUTH_HEADER_NAME        |              HTTP header name which contains JWT             |                             Authorization                             |
|      ivana-chess.auth.header.value-prefix     |    IVANA_CHESS_AUTH_HEADER_VALUE_PREFIX    |        Prefix of HTTP header value which prefixes JWT        |                                Bearer                                 |
|          ivana-chess.auth.cookie.name         |        IVANA_CHESS_AUTH_COOKIE_NAME        |                Name of cookie used to send JWT               |                          _ivana_chess_session                         |
|         ivana-chess.auth.cookie.domain        |       IVANA_CHESS_AUTH_COOKIE_DOMAIN       |                       Domain of cookie                       |                               localhost                               |
|         ivana-chess.auth.cookie.secure        |       IVANA_CHESS_AUTH_COOKIE_SECURE       |             If cookie secure attribute is enabled            |                                 false                                 |
|       ivana-chess.auth.cookie.http-only       |      IVANA_CHESS_AUTH_COOKIE_HTTP_ONLY     |           If cookie http only attribute is enabled           |                                  true                                 |
|           ivana-chess.logging.config          |         IVANA_CHESS_LOGGING_CONFIG         |              Path to Logback configuration file              |                         classpath:logback.xml                         |
