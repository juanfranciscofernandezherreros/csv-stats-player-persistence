![version](https://img.shields.io/badge/version-1.1.0-blue)
# csv-stats-player-persistence

Microservicio de persistencia separado de `csv-stats-player-consumer`.

```text
stats-player.parsed -> ParsedStatsPlayerConsumer -> JPA -> PostgreSQL stats_player
```

Consume exactamente `StatsPlayerKey` / `StatsPlayerValue` publicados por `csv-stats-player-parser`.

## Contratos Avro compartidos

`StatsPlayerKey` y `StatsPlayerValue` se consumen desde:

```text
com.fernandez.basketball:basketball-event-contracts:1.0.2
```

Este repositorio ya no mantiene copias locales de esos schemas ni genera las clases Avro durante su propia build. Fuera de GitHub Actions, Maven necesita credenciales con `read:packages` para resolver el artefacto desde GitHub Packages.

La tabla conserva la estructura histórica `stats_player` y la unicidad `(match_id, name, team)`. Ante una reentrega, el servicio hace upsert y conserva el último `source_event_id`.

Hibernate usa identificadores entrecomillados para mantener compatibilidad con las columnas históricas `"or"` y `"to"` de PostgreSQL.

Variables: `DB_URL`, `DB_USER`, `DB_PASS`, `KAFKA_BOOTSTRAP_SERVERS`, `KAFKA_SCHEMA_REGISTRY_URL`, `KAFKA_PARSED_STATS_PLAYER_TOPIC`.

Tests: `mvn -B test`. Integración PostgreSQL: `mvn -B verify -Pintegration`.


## Estrategia de errores Kafka

KAN-109 aplica la política de KAN-18 al consumo de `stats-player.parsed`.

- errores de datos o integridad: non-retryable;
- fallos transitorios de PostgreSQL: retryable;
- mensajes agotados: `stats-player.parsed.DLT`;
- `KAFKA_RETRY_MAX_ATTEMPTS`: intentos totales, default `3`;
- `KAFKA_RETRY_BACKOFF_MS`: backoff fijo, default `1000`;
- `KAFKA_PLAYER_PERSISTENCE_DLT_TOPIC`: topic DLT configurable.

Spring Kafka publica el registro original en DLT con headers de excepción y contexto.
