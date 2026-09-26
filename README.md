![version](https://img.shields.io/badge/version-1.2.0-blue)
# csv-stats-player-persistence

Microservicio de persistencia separado de `csv-stats-player-consumer`.

```text
stats-player.parsed -> ParsedStatsPlayerConsumer -> PostgreSQL stats_player
```

Consume exactamente `StatsPlayerKey` / `StatsPlayerValue` publicados por `csv-stats-player-parser`.

## Política de idempotencia

KAN-53 aplica KAN-19 a PLAYER. La clave natural es:

```text
(match_id, name, team)
```

PostgreSQL la protege con `uk_stats_player_match_player_team`. La escritura ya no hace `findByMatchIdAndNameAndTeam() + save()`; usa una única operación:

```sql
INSERT ...
ON CONFLICT (match_id, name, team)
DO UPDATE SET ...;
```

La política es current-state upsert: un redelivery idéntico mantiene una sola fila y una reimportación actualiza las estadísticas del jugador. `source_event_id` queda actualizado al evento que produjo el estado vigente. La operación es segura frente a dos redeliveries concurrentes para la misma clave natural.

## Contratos Avro compartidos

`StatsPlayerKey` y `StatsPlayerValue` se consumen desde:

```text
com.fernandez.basketball:basketball-event-contracts:1.0.2
```

Este repositorio ya no mantiene copias locales de esos schemas ni genera las clases Avro durante su propia build. Fuera de GitHub Actions, Maven necesita credenciales con `read:packages` para resolver el artefacto desde GitHub Packages.

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

El consumer usa `ErrorHandlingDeserializer` para que un Avro corrupto entre en el flujo normal de recuperación. La DLT acepta tanto objetos Avro como los `byte[]` originales, deja que Kafka seleccione una partición válida, conserva los headers de excepción de Spring Kafka y hace visible cualquier fallo al publicar en DLT.
