![version](https://img.shields.io/badge/version-1.3.0-blue)
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

## Persistencia batch

KAN-22 cambia el camino Kafka de PLAYER a procesamiento por poll:

```text
Kafka poll (hasta 500)
        ↓
ParsedStatsPlayerConsumer
        ↓
persistBatch(...)
        ↓
JdbcTemplate.batchUpdate(...)
        ↓
PostgreSQL
```

`KAFKA_MAX_POLL_RECORDS` controla el tamaño máximo del poll y vale `500` por defecto. El driver PostgreSQL usa `reWriteBatchedInserts=true` para reducir round-trips. El batch conserva el mismo `ON CONFLICT (match_id, name, team) DO UPDATE` de KAN-53, por lo que redeliveries del poll siguen siendo idempotentes.

Si una escritura del batch falla, falla la transacción y el error vuelve a la estrategia Kafka de retry/DLT existente. No se confirma parcialmente un poll desde el servicio de persistencia.

La suite de integración ejecuta una medición con 1.000 jugadores comparando el upsert secuencial con `JdbcTemplate.batchUpdate` y publica ambos throughputs y su ratio en el log de CI. El test verifica corrección y registra la medida sin imponer un umbral temporal frágil.

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
